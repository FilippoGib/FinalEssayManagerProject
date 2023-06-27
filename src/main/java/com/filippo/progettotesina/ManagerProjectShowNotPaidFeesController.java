package com.filippo.progettotesina;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;


public class ManagerProjectShowNotPaidFeesController {
    @FXML
    private Label fullNameLabel;
    @FXML
    private TableView<Fee> feeTableView;
    @FXML
    private TableColumn<Fee, String> expiredDateColumn;
    private Person person;
    public static HikariDataSource dataSource;

    public static final String JDBC_Driver_MySQL = "com.mysql.cj.jdbc.Driver";

    public static final String JDBC_URL_MySQL = "jdbc:mysql://localhost:3306/people?user=common&password=common123456";

    public void initialize(){
        //delay the execution of the initializing code so that i can use setPerson before Person methods are called
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            expiredDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiry"));
            dbConnection();
            feeTableView.setItems(getCorrespondingFees(this.person));
            fullNameLabel.setText(this.person.getFirstName()+" "+this.person.getLastName());
        }
        );
        delay.play();
    }
    private void dbConnection() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_Driver_MySQL);
        config.setJdbcUrl(JDBC_URL_MySQL);
        config.setLeakDetectionThreshold(2000);
        dataSource = new HikariDataSource(config);
    }
    public ObservableList<Fee> getCorrespondingFees(Person person){
        ObservableList<Fee> fees=FXCollections.observableArrayList();
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement getFees = connection.prepareStatement("SELECT f.* FROM fee as f join fee_not_paid fnp on f.id = fnp.fee_id where fnp.person_id=? ");
            getFees.setInt(1,person.getID());
            ResultSet resultSet = getFees.executeQuery();
            while (resultSet.next()) {
                fees.add(new Fee(resultSet.getInt("ID"),resultSet.getDouble("amount"),resultSet.getDate("expiry").toLocalDate(),false));
               }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
        }
        return fees;
    }
    public void setPerson(Person person) {
        this.person=person;
        initialize();
    }
    @FXML
    void handlePaid(){
        try {
            int selectedIndex = selectedIndex();
            //connessione al DB
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement paid=connection.prepareStatement("DELETE from fee_not_paid where fee_id=? and person_id=?");
                paid.setInt(1,feeTableView.getItems().get(selectedIndex).getID());
                paid.setInt(2,this.person.getID());
                paid.executeUpdate();

            } catch(SQLException s){
                s.printStackTrace();
            }
            feeTableView.getItems().remove(selectedIndex());
        }catch(NoSuchElementException n){
            showNoFeeSelectedAlert();
        }
    }
    private int selectedIndex() {
        int selectedIndex = feeTableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();
        }
        return selectedIndex;
    }
    void showNoFeeSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nessuna selezione");
        alert.setHeaderText("Nessuna quota selezionata");
        alert.setContentText("Per favore seleziona una quota da cancellare");
        alert.showAndWait();
    }

}

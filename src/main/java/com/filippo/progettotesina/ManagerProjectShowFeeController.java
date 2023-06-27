package com.filippo.progettotesina;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;

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
public class ManagerProjectShowFeeController {
    @FXML
    private TableView<Fee> feeTable;
    @FXML
    private TableColumn<Fee, String> expiredDateColumn;
    @FXML
    private TableColumn<Fee, String> amountColumn;

    public static HikariDataSource dataSource;

    public static final String JDBC_Driver_MySQL = "com.mysql.cj.jdbc.Driver";

    public static final String JDBC_URL_MySQL = "jdbc:mysql://localhost:3306/people?user=common&password=common123456";

    public void initialize(){
        expiredDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiry"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dbConnection();
        feeTable.setItems(getFeeData());

    }
    private void dbConnection() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_Driver_MySQL);
        config.setJdbcUrl(JDBC_URL_MySQL);
        config.setLeakDetectionThreshold(2000);
        dataSource = new HikariDataSource(config);
    }
    public ObservableList<Fee> getFeeData(){
        ObservableList<Fee> fees= FXCollections.observableArrayList();
        try{
            Connection connection = dataSource.getConnection();
            PreparedStatement getFee= connection.prepareStatement("Select * from fee");
            ResultSet resultSet = getFee.executeQuery();
            while(resultSet.next()){
                fees.add(new Fee(resultSet.getInt("ID"),resultSet.getDouble("amount"),resultSet.getDate("expiry").toLocalDate(),false));
            }
        } catch (SQLException e){
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();

        }
        return fees;
    }

    @FXML
    void handleDeleteFee(){
        try{
            int selectedIndex=selectedIndex();
            try{
                Connection connection=dataSource.getConnection();
                PreparedStatement deleteFee=connection.prepareStatement("Delete from fee where id=?");
                PreparedStatement deleteFeeNotPaid=connection.prepareStatement("delete from fee_not_paid where fee_id=?");
                deleteFeeNotPaid.setInt(1,feeTable.getItems().get(selectedIndex).getID());
                deleteFee.setInt(1,feeTable.getItems().get(selectedIndex).getID());
                deleteFeeNotPaid.executeUpdate();
                deleteFee.executeUpdate();

            } catch(SQLException e){
                e.printStackTrace();
            }
            feeTable.getItems().remove(selectedIndex);

        }catch(NoSuchElementException n){
            showNoFeeSelectedAlert();
        }
    }
    void showNoFeeSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nessuna selezione");
        alert.setHeaderText("Nessuna quota selezionata");
        alert.setContentText("Per favore seleziona una quota da cancellare");
        alert.showAndWait();
    }
    int selectedIndex() {
        int selectedIndex = feeTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();
        }
        return selectedIndex;
    }

}

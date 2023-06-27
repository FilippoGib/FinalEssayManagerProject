package com.filippo.progettotesina;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

public class ManagerProjectAnnouncementController {
    //the next four attributes are for the first view
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;
    @FXML
    private TableColumn<Person, String> expiredDateColumn;

    //the next four attributes are for the second view
    @FXML
    private TableView<Person> personFeesTable;
    @FXML
    private TableColumn<Person, String> firstNameFeesColumn;
    @FXML
    private TableColumn<Person, String> lastNameFeesColumn;
    @FXML
    private TableColumn<Person, String> dueFeesColumn;
    private ObservableList<Person> people;

    Fee fee;

    public static HikariDataSource dataSource;

    public static final String JDBC_Driver_MySQL = "com.mysql.cj.jdbc.Driver";

    public static final String JDBC_URL_MySQL = "jdbc:mysql://localhost:3306/people?user=common&password=common123456";


    public void initialize(){
        // Initialize the person table with the three columns.
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        expiredDateColumn.setCellValueFactory(new PropertyValueFactory<>("medicalExamExpiryDate"));

        // Initialize the personFees table with the three columns.
        firstNameFeesColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameFeesColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dueFeesColumn.setCellValueFactory(new PropertyValueFactory<>("paidFees"));


        dbConnection();
        personTable.setItems(getPersonData().stream().filter(p->isExpiredMedicalExam(p))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        personFeesTable.setItems(getPersonData().stream().filter(p->!p.isPaidFees() || oldFeesNotPaid(p))
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));

        dueFeesColumn.setCellValueFactory(cellData -> {
            Person person = cellData.getValue();
            int unpaidFeesCount = getUnpaidFeesCount(person.getID());
            if(unpaidFeesCount==0) unpaidFeesCount++;
            return new SimpleStringProperty(String.valueOf(unpaidFeesCount)); //Current fee included
        });

    }
    public boolean oldFeesNotPaid(Person person){
        boolean ret= false;
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement notPaid = connection.prepareStatement("select * from fee_not_paid where person_id = ?");
            notPaid.setInt(1,person.getID());
            ResultSet p = notPaid.executeQuery();
            if(p.next()){
                ret=true;
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
        }
        return ret;
    }

    public ObservableList<Person> getPersonData() {

        people = FXCollections.observableArrayList();

        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement getPeople = connection.prepareStatement("SELECT * FROM people");
            ResultSet resultSet = getPeople.executeQuery();
            while (resultSet.next()) {
                people.add(new Person(resultSet.getInt("ID"), resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("street"), resultSet.getString("city"), resultSet.getDate("birthday").toLocalDate(), resultSet.getDate("medicalExamExpiryDate").toLocalDate(), resultSet.getBoolean("paidFees")));
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
        }
        System.out.println(people);
        return people;
    }
    private void dbConnection() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_Driver_MySQL);
        config.setJdbcUrl(JDBC_URL_MySQL);
        config.setLeakDetectionThreshold(2000);
        dataSource = new HikariDataSource(config);
    }
    public Boolean isExpiredMedicalExam(Person person) {
        Date dateMedicalExamExpiryDate = Date.from(person.getMedicalExamExpiryDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date dateNow = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return dateNow.after(dateMedicalExamExpiryDate);
    }
    private int getUnpaidFeesCount(int personId) {
        int unpaidFeesCount = 0;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM fee_not_paid WHERE person_id = ?")) {
            statement.setInt(1, personId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                unpaidFeesCount = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unpaidFeesCount;
    }
    @FXML
    void handleShowNotPaidFees(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerProject-ShowNotPaidFees-view.fxml"));
            Parent root;
            int selected = selectedIndex();
            try {
                Person p=new Person(personFeesTable.getItems().get(selected));
                root = loader.load();
                ManagerProjectShowNotPaidFeesController controller = loader.getController();
                controller.setPerson(p); //create a new person from person and setting it into the controller
                controller.initialize();
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (NoSuchElementException n){
            showNoPersonSelectedAlert();
        }
    }

    void showNoPersonSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nessuna selezione");
        alert.setHeaderText("Nessuna persona selezionata");
        alert.setContentText("Per favore seleziona una persona da cancellare");
        alert.showAndWait();
    }
    int selectedIndex() {
        int selectedIndex = personFeesTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();

        }
        return selectedIndex;
    }


}


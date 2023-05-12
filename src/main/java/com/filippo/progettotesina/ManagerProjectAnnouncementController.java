package com.filippo.progettotesina;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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
        personFeesTable.setItems(getPersonData().stream().filter(p->!p.isPaidFees())
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));


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
}

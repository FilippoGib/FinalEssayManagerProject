package com.filippo.progettotesina;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.sql.*;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javafx.stage.Stage;

import static com.filippo.progettotesina.Person.getMaxID;

public class ManagerProjectController {

    @FXML
    private Label birthdayLabel;

    @FXML
    private Label cityLabel;

    @FXML
    private TableColumn<Person, String> firstNameColumn;

    @FXML
    private Label firstNameLabel;

    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label medicalExamExpiryDateLabel;

    @FXML
    private Label paidFeesLabel;

    @FXML
    private TableView<Person> personTable;

    @FXML
    private Label streetLabel;

    private File file = null;

    public static final String JDBC_Driver_MySQL = "com.mysql.cj.jdbc.Driver";

    public static final String JDBC_URL_MySQL = "jdbc:mysql://localhost:3306/people?user=common&password=common123456";

    private ObservableList<Person> people;

    public static HikariDataSource dataSource;

    public void initialize() {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        dbConnection();
        personTable.setItems(getPersonData());

        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));
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

        return people;
    }

    void insertDBPerson(Person person) {
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement insertPerson = connection.prepareStatement("INSERT INTO people (ID, firstName, lastName, street, city, birthday, medicalExamExpiryDate, paidFees) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
                insertPerson.setInt(1, getMaxID() + 1);
                insertPerson.setString(2, person.getFirstName());
                insertPerson.setString(3, person.getLastName());
                insertPerson.setString(4, person.getStreet());
                insertPerson.setString(5, person.getCity());
                insertPerson.setDate(6, Date.valueOf(person.getBirthday()));
                insertPerson.setDate(7, Date.valueOf(person.getMedicalExamExpiryDate()));
                insertPerson.setBoolean(8, person.isPaidFees());
            insertPerson.executeUpdate();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
        }
    }

    private void dbConnection() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(JDBC_Driver_MySQL);
        config.setJdbcUrl(JDBC_URL_MySQL);
        config.setLeakDetectionThreshold(2000);
        dataSource = new HikariDataSource(config);
    }

    private void showPersonDetails(Person person) {
        if (person != null) {
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            medicalExamExpiryDateLabel.setText(person.getMedicalExamExpiryDate().toString());
            paidFeesLabel.setText(getStringPaidFees(person.isPaidFees()));
            cityLabel.setText(person.getCity());
            birthdayLabel.setText(person.getBirthday().toString());
        } else {
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            medicalExamExpiryDateLabel.setText("");
            cityLabel.setText("");
            birthdayLabel.setText("");
            paidFeesLabel.setText("");
        }
    }

    public String getStringPaidFees(boolean paidFees) {
        if (paidFees) {
            return "saldata";
        } else
            return "da saldare";
    }

    int selectedIndex() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex < 0) {
            throw new NoSuchElementException();
        }
        return selectedIndex;
    }

    /**
     * Shows a simple warning dialog
     */
    void showNoPersonSelectedAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nessuna selezione");
        alert.setHeaderText("Nessuna persona selezionata");
        alert.setContentText("Per favore seleziona una persona da cancellare");
        alert.showAndWait();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Address Application");
        alert.setHeaderText("Informazioni");
        alert.setContentText("Autore: Filippo Gibertini");
        alert.showAndWait();
    }

    @FXML
    private void handleSupport() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Address Application");
        alert.setHeaderText("Supporto");
        alert.setContentText("contatti e-mail: filippogib@gmail.com\ncontatti telefonici: +39 3209309088");
        alert.showAndWait();
    }

    @FXML
    void handleClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void handleExpiredMedicalExam(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ManagerProject-Announcement-view.fxml"));
            TabPane view = loader.load();
            ManagerProjectAnnouncementController controller = loader.getController();

            Scene scene = new Scene(view);
            Stage stage = new Stage();
            stage.setTitle("Avvisi");
            stage.setScene(scene);
            stage.show();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }





    @FXML
    void handleDeletePerson(ActionEvent event) {
        try {
            int selectedIndex = selectedIndex();

            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement deletePerson = connection.prepareStatement("DELETE FROM people where ID=?");
                deletePerson.setInt(1, personTable.getItems().get(selectedIndex).getID());
                deletePerson.executeUpdate();


            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
            }

            personTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    @FXML
    public void handleEditPerson() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ManagerProject-edit-view.fxml"));
            DialogPane view = loader.load();
            ManagerProjectEditController controller = loader.getController();

            // Set the person into the controller.
            int selectedIndex = selectedIndex();
            controller.setPerson(new Person(personTable.getItems().get(selectedIndex)));

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Edita Persona");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            // Show the dialog and wait until the user closes it
            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                personTable.getItems().set(selectedIndex, controller.getPerson());
            }
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleNewFile(ActionEvent event) {
        personTable.getItems().clear();
    }

    @FXML
    public void handleNewPerson() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("ManagerProject-edit-view.fxml"));
            DialogPane view = loader.load();
            ManagerProjectEditController controller = loader.getController();

            // Set an empty person into the controller
            controller.setPerson(new Person(getMaxID() + 1, "First Name", "Last Name", "Street", "City",
                    LocalDate.now(),
                    LocalDate.now(), true));

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Nuova Persona");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            // Show the dialog and wait until the user closes it
            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                personTable.getItems().add(controller.getPerson());
                insertDBPerson(controller.getPerson());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleNotPaidFees(ActionEvent event) {

    }

    @FXML
    private void handleOpen() throws Exception {
         try {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        file = fileChooser.showOpenDialog(null);
        if (file != null) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            List<Person> persons = mapper.readValue(file, new TypeReference<List<Person>>() {
            });
            personTable.getItems().addAll(persons);
        }
         } catch (IOException e) {
        new Alert(Alert.AlertType.ERROR, "Impossibile caricare dati").showAndWait();
        }
    }

    @FXML
    private void handleSaveAs() {
        try {
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
            fileChooser.getExtensionFilters().add(extFilter);

            file = fileChooser.showSaveDialog(null);
            if (file != null) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, personTable.getItems());
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not save data").showAndWait();
        }
    }

    @FXML
    private void handleSave() {
        try {

            if (file != null) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.registerModule(new JavaTimeModule());
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, personTable.getItems());
            } else {
                handleSaveAs();
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Could not save data").showAndWait();
        }
    }
}

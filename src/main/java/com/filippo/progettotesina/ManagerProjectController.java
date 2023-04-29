package com.filippo.progettotesina;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.NoSuchElementException;

public class TesinaController {

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

    public void initialize() {
        // Initialize the person table with the two columns.
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        personTable.setItems(getPersonData());

        // Clear person details.
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed.
        personTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showPersonDetails(newValue));
    }
    public ObservableList<Person> getPersonData() {

        ObservableList<Person> persons = FXCollections.observableArrayList();
        persons.add(new Person("Filippo", "Gibertini","via burchi","Modena", LocalDate.of(2002, 4, 18),LocalDate.of(2024,4,29),true));
     /*   persons.add(new Person("Thomas", "Mueller"));
        persons.add(new Person("Benito", "Mussolini"));
        persons.add(new Person("Corinna", "Negri"));
        persons.add(new Person("Stanis", "La Rochelle"));
        persons.add(new Person("Lydia", "Kunz"));
        persons.add(new Person("Anna", "Best"));
        persons.add(new Person("Stefan", "Meier"));
        persons.add(new Person("Martin", "Mueller"));*/
        return persons;
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
    public String getStringPaidFees(boolean paidFees){
        if(paidFees){
            return "saldata";
        }
        else return "da saldare";
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
    private void handleAbout () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Address Application");
        alert.setHeaderText("Informazioni");
        alert.setContentText("Autore: Filippo Gibertini");
        alert.showAndWait();
    }
    @FXML
    private void handleSupport () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Address Application");
        alert.setHeaderText("Supporto");
        alert.setContentText("contatti e-mail: filippogib@gmail.com\ncontatti telefonici: +39 3209309088");
        alert.showAndWait();
    }


    @FXML
    void handleClose(ActionEvent event) {

    }
    @FXML
    void handleExpiredMedicalExam(ActionEvent event) {

    }

    @FXML
    void handleDeletePerson(ActionEvent event) {
        try {
            int selectedIndex = selectedIndex();
            personTable.getItems().remove(selectedIndex);
        } catch (NoSuchElementException e) {
            showNoPersonSelectedAlert();
        }
    }

    @FXML
    void handleEditPerson(ActionEvent event) {

    }

    @FXML
    void handleNewFile(ActionEvent event) {

    }

    @FXML
    void handleNewPerson(ActionEvent event) {

    }

    @FXML
    void handleNotPaidFees(ActionEvent event) {

    }

    @FXML
    void handleOpen(ActionEvent event) {

    }

    @FXML
    void handleSaveAs(ActionEvent event) {

    }

}

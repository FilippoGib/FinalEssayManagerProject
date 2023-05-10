package com.filippo.progettotesina;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ManagerProjectEditController {
    @FXML
    private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private DatePicker medicalExamExpiryDatePicker;
    @FXML private DatePicker birthdayDatePicker;
    @FXML private RadioButton paidButton;
    @FXML private RadioButton dueButton;
    @FXML
    private ToggleGroup paymentStatusToggleGroup;
    Person person;
    @FXML
    public void initialize() {
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> person.firstNameProperty().set(newValue));
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> person.lastNameProperty().set(newValue));
        streetField.textProperty().addListener((observable, oldValue, newValue) -> person.streetProperty().set(newValue));
        cityField.textProperty().addListener((observable, oldValue, newValue) -> person.cityProperty().set(newValue));
        birthdayDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> person.birthdayProperty().set(newValue));
        medicalExamExpiryDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> person.medicalExamExpiryDateProperty().set(newValue));


        paymentStatusToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == paidButton) {
                person.setPaidFees(true);
            } else if (newValue == dueButton) {
                person.setPaidFees(false);
            }
        });
    }
    void update() {
        firstNameField.textProperty().set(person.getFirstName());
        lastNameField.textProperty().set(person.getLastName());
        streetField.textProperty().set(person.getStreet());
        cityField.textProperty().set(person.getCity());
        birthdayDatePicker.valueProperty().set(person.getBirthday());
        medicalExamExpiryDatePicker.valueProperty().set(person.getMedicalExamExpiryDate());
        paidButton.setSelected(person.isPaidFees());
        dueButton.setSelected(!person.isPaidFees());
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        update();
    }
}

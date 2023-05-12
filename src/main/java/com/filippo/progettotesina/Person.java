package com.filippo.progettotesina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;

import static com.filippo.progettotesina.ManagerProjectController.dataSource;

public class Person {
    private int ID;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final StringProperty city;
    private final ObjectProperty<LocalDate> birthday;
    private final ObjectProperty<LocalDate> medicalExamExpiryDate;
    private final BooleanProperty paidFees;

    public Person() {
        this(null, null);
    }

    public Person(String firstName, String lastName) {
        this(getMaxID() + 1, firstName, lastName, "Random Street", "Nowhere", LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 1), true);
    }

    public Person(int ID, String firstName, String lastName, String street, String city, LocalDate birthday, LocalDate medicalExamExpiryDate, boolean paidFees) {
        this.ID = ID;
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.street = new SimpleStringProperty(street);
        this.city = new SimpleStringProperty(city);
        this.birthday = new SimpleObjectProperty<>(birthday);
        this.medicalExamExpiryDate = new SimpleObjectProperty<>(medicalExamExpiryDate);
        this.paidFees = new SimpleBooleanProperty(paidFees);
    }

    public Person(Person person) {
        this.firstName = new SimpleStringProperty(person.getFirstName());
        this.lastName = new SimpleStringProperty(person.getLastName());
        this.street = new SimpleStringProperty(person.getStreet());
        this.city = new SimpleStringProperty(person.getCity());
        this.birthday = new SimpleObjectProperty<>(person.getBirthday());
        this.medicalExamExpiryDate = new SimpleObjectProperty<>(person.getMedicalExamExpiryDate());
        this.paidFees = new SimpleBooleanProperty(person.isPaidFees());
    }

    public int getID() { return ID; }

    public void setID(int ID) { this.ID = ID; }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public String getStreet() {
        return street.get();
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public StringProperty streetProperty() {
        return street;
    }

    public String getCity() {
        return city.get();
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public StringProperty cityProperty() {
        return city;
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public ObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }

    public LocalDate getMedicalExamExpiryDate() {
        return medicalExamExpiryDate.get();
    }

    public void setMedicalExamExpiryDate(LocalDate medicalExamExpiryDate) {
        this.medicalExamExpiryDate.set(medicalExamExpiryDate);
    }

    public ObjectProperty<LocalDate> medicalExamExpiryDateProperty() {
        return medicalExamExpiryDate;
    }

    public boolean isPaidFees() {
        return paidFees.get();
    }

    public void setPaidFees(boolean paidFees) {
        this.paidFees.set(paidFees);
    }

    public BooleanProperty paidFeesProperty() {
        return paidFees;
    }

   /* public Boolean isExpiredMedicalExam() {
        Date dateMedicalExamExpiryDate = Date.from(this.medicalExamExpiryDate.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date dateNow = Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        return dateNow.after(dateMedicalExamExpiryDate);
    }*/

    public static int getMaxID() {
        int maxID = 0;
        try {
            Connection connection = dataSource.getConnection();
            PreparedStatement getMaxID = connection.prepareStatement("SELECT MAX(ID) FROM people WHERE ID is not NULL");
            ResultSet resultSet = getMaxID.executeQuery();
            resultSet.next();
            maxID = resultSet.getInt(1);
            return maxID;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Database Error").showAndWait();
        }
        return maxID;
    }
}

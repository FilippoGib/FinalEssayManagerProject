package com.filippo.progettotesina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import javafx.beans.property.*;
import javafx.scene.control.Alert;

import static com.filippo.progettotesina.ManagerProjectController.dataSource;
public class Fee {
    private int ID;
    private DoubleProperty amount;
    private ObjectProperty<LocalDate> expiry;

    public Fee(int ID, double amount, LocalDate expiry) {
        this.ID = ID;
        this.amount = new SimpleDoubleProperty(amount);
        this.expiry = new SimpleObjectProperty<>(expiry);
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public double getAmount() {
        return amount.get();
    }

    public DoubleProperty amountProperty() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount.set(amount);
    }

    public LocalDate getExpiry() {
        return expiry.get();
    }

    public ObjectProperty<LocalDate> expiryProperty() {
        return expiry;
    }

    public void setExpiry(LocalDate expiry) {
        this.expiry.set(expiry);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Fee fee = (Fee) o;
        return ID == fee.ID && Objects.equals(amount, fee.amount) && Objects.equals(expiry, fee.expiry);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, amount, expiry);
    }
}

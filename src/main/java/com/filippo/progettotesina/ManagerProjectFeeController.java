package com.filippo.progettotesina;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ManagerProjectFeeController {
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker feeExpiryDatePicker;
    Fee fee;
    @FXML
    public void initialize(){
        amountField.textProperty().addListener((observable,oldValue,newValue)->fee.amountProperty().set(parseDouble(newValue)));
        feeExpiryDatePicker.valueProperty().addListener((observable, oldValue, newValue) ->fee.expiryProperty().set(newValue));
    }
    void update(){
        amountField.textProperty().set(Double.toString(fee.getAmount()));
        feeExpiryDatePicker.valueProperty().set(fee.getExpiry());
    }
    public Fee getFee(){
        return fee;
    }
    public void setFee(Fee fee){
        this.fee=fee;
        update();
    }

    @Override
    public String toString() {
        return "ManagerProjectFeeController{" +
                "amountField=" + amountField +
                ", feeExpiryDatePicker=" + feeExpiryDatePicker +
                ", fee=" + fee +
                '}';
    }
}

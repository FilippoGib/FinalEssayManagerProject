module com.filippo.progettotesina {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;


    opens com.filippo.progettotesina to javafx.fxml;
    exports com.filippo.progettotesina;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
}
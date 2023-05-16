module com.example.iae {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires com.google.gson;
    requires org.xerial.sqlitejdbc;
    requires com.fasterxml.jackson.databind;


    opens com.example.iae.ce316 to javafx.fxml ,com.google.gson;
    exports com.example.iae.ce316;
}
package com.filippo.progettotesina;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ManagerProjectApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ManagerProjectApplication.class.getResource("ManagerProject-overview-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Academy Management");
        stage.setScene(scene);
        stage.show();
        //commento per commit
    }

    public static void main(String[] args) {
        launch();
    }
}
package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class WebServerGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("WebServerGUI.fxml")));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Web server");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package skvortsov.best.pupil.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartClient extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Chat NextGEN!");
        stage.setScene(scene);
        stage.show();
    }
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void windowAbout(Stage stageAbout) throws IOException {
        Scene sceneAbout = new Scene(loadFXML("aboutWindow"));
        stageAbout.setScene(sceneAbout);
        stageAbout.setTitle("About APP");
        stageAbout.centerOnScreen();
        stageAbout.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
package skvortsov.best.pupil.chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import skvortsov.best.pupil.chat.client.controllers.AuthController;
import skvortsov.best.pupil.chat.client.controllers.ChatController;
import skvortsov.best.pupil.chat.client.models.Network;

import java.io.IOException;

public class StartClient extends Application {

    private Network network;
    private Stage primaryStage;
    private Stage authStage;
    private ChatController chatController;

    @Override
    public void start(Stage stage) throws IOException {

        this.primaryStage = stage;

        network = new Network();
        network.connect();

        openAuthDialog();
        createChatDialog();
    }

    private void openRegOrAuthDialog() {

    }

    private void openAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader(StartClient.class.getResource("auth-view.fxml"));
        Scene scene = new Scene(authLoader.load());
        authStage = new Stage();
        authStage.setScene(scene);
        authStage.initModality(Modality.WINDOW_MODAL);
        authStage.initOwner(primaryStage);
        authStage.setTitle("Chat NextGEN!");
        authStage.show();

        AuthController authController = authLoader.getController();
        authController.setNetwork(network);
        authController.setStartClient(this);
    }

    private void createChatDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartClient.class.getResource("chat-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setScene(scene);

        chatController = fxmlLoader.getController();
        chatController.setNetwork(network);
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

    public void openChatDialog() {
        authStage.close();
        primaryStage.show();
        primaryStage.setTitle("Chat NextGEN! - " + network.getUsername());
        network.waitMessage(chatController);
        chatController.setUsernameLabel(network.getUsername());
    }

    public void showErrorAlert(String title, String errorMessage){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(errorMessage);
        alert.show();
    }
}

// Попроюовать сделать лист в стартКлаенте и получать его здесь и грузить при открытии
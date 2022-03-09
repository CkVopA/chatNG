module safronov.best.teacher.chat {
    requires javafx.controls;
    requires javafx.fxml;


    opens skvortsov.best.pupil.chat.client to javafx.fxml;
    exports skvortsov.best.pupil.chat.client;
    exports skvortsov.best.pupil.chat.client.controllers;
    opens skvortsov.best.pupil.chat.client.controllers to javafx.fxml;
}
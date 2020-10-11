package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.IOException;

public class AlertPopupController {

    @FXML
    private HBox mainPane;

    @FXML
    private Label massageLabel;

    private Stage stage;
    private Parent paneBehind;

    public AlertPopupController(Parent paneBehind) {
        this.paneBehind = paneBehind;
    }

    public void initialize() {
        massageLabel.setText(Utils.WRONG_IP_ADDRESS_MASSAGE);
    }

    private HBox loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.ALERT_POPUP));
        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void show() {
        HBox hBox = loadView();
        FadeTransition ft = new FadeTransition(Duration.millis(1000), hBox);
        ft.setFromValue(0.5);
        ft.setToValue(1.0);
        ft.play();
        Scene scene = new Scene(hBox);

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());

        autoClosePopUpTimer();
        stage.show();
        locateStageDownToParentStage();
    }

    private void autoClosePopUpTimer() {
        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> stage.close());
        delay.play();
    }

    private void locateStageDownToParentStage() {
        Stage primaryStage = (Stage) paneBehind.getScene().getWindow();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight();

        stage.setX(centerXPosition - stage.getWidth() / 2d);
        stage.setY(centerYPosition - stage.getHeight());
    }
}

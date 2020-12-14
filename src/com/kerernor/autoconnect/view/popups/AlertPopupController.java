package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AlertPopupController {

    @FXML
    private BorderPane mainPane;
    @FXML
    private Label massageLabel;
    @FXML
    private HBox closePopupIcon;
    @FXML
    private ImageView alertIcon;

    private Logger logger = LogManager.getLogger(AlertPopupController.class);
    private Stage stage;
    private static Queue<AlertPopupController> queue = new ArrayDeque<>();
    BorderPane borderPane;
    Parent paneBehind;
    Scene scene;
    private long TIME_TO_CLOSE_ALERT;
    ScheduledFuture<?> timer;

    public AlertPopupController() {
        borderPane = loadView();
        scene = new Scene(borderPane);
    }

//    public static AlertPopupController getInstance() {
//        if (instance == null) {
//            instance = new AlertPopupController();
//
//        }
//        return instance;
//    }

    public void initialize() {

    }

    private BorderPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.ALERT_POPUP));
        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void showAlert(KorTypes.AlertTypes alertType, String msg, Parent paneBehind) {
        queue.add(this);
        this.paneBehind = paneBehind;
        massageLabel.setText(msg);
        configAlertColorAndImage(alertType);
        FadeTransition ft = new FadeTransition(Duration.millis(1000), mainPane);
        ft.setFromValue(0.5);
        ft.setToValue(1.0);
        ft.play();

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());

        autoClosePopUpTimer();
        timer = ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
            Platform.runLater(this::autoClosePopUpTimer);
        }, TIME_TO_CLOSE_ALERT, TimeUnit.MILLISECONDS);
        stage.show();
        locateStageDownToParentStage();
    }

    private void autoClosePopUpTimer() {
        if (timer != null) {
            logger.debug("Close stage");
            timer.cancel(true);
            stage.hide();
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> stage.close());
            delay.play();
            timer = null;
        }

    }

    private void locateStageDownToParentStage() {
        Stage primaryStage = (Stage) paneBehind.getScene().getWindow();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight();

        stage.setX(centerXPosition - stage.getWidth() / 2d);
        stage.setY(centerYPosition - stage.getHeight());
    }

    @FXML
    public void autoClosePopUpTimerHandler() {
        logger.debug("close alert by click");
        autoClosePopUpTimer();
    }

    private void configAlertColorAndImage(KorTypes.AlertTypes alertType) {
        massageLabel.setTextFill(Color.BLACK);
        switch (alertType) {
            case INFO:
                borderPane.setStyle("-fx-background-color: #8ca4f5");
                TIME_TO_CLOSE_ALERT = Utils.TIME_FOR_CLOSE_ALERT_MESSAGE_INFO;
                alertIcon.setImage(Utils.getImageByName(Utils.ALERT_INFO_ICON));
                break;
            case WARNING:
                borderPane.setStyle("-fx-background-color: #ff9800");
                TIME_TO_CLOSE_ALERT = Utils.TIME_FOR_CLOSE_ALERT_MESSAGE_WARNING;
                alertIcon.setImage(Utils.getImageByName(Utils.ALERT_WARNING_ICON));
                break;
            case ERROR:
                borderPane.setStyle("-fx-background-color: #ff112a");
                TIME_TO_CLOSE_ALERT = Utils.TIME_FOR_CLOSE_ALERT_MESSAGE_ERROR;
                alertIcon.setImage(Utils.getImageByName(Utils.ALERT_ERROR_ICON));
                break;
            default:
        }
    }

    public static void sendAlert(KorTypes.AlertTypes alertType, String msg, Parent paneBehind) {
        AlertPopupController alertPopupController = new AlertPopupController();
        alertPopupController.showAlert(alertType, msg, paneBehind);
    }

}

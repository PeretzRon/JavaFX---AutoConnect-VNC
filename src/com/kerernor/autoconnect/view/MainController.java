package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.view.screens.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController extends AnchorPane {

    @FXML
    private StackPane stackPaneAllScreens;
    @FXML
    private RemoteScreenController remoteScreenController;
    @FXML
    private PingerScreenController pingerScreenController;
    @FXML
    private AboutScreenController aboutScreenController;
    @FXML
    private RemoteDriveScreenController remoteDriveScreenController;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private Button btnRemoteScreen;
    @FXML
    private Button btnExitApp;
    @FXML
    private Button btnPingerScreen;
    @FXML
    private Button btnOpenWindowScreen;
    @FXML
    private Button btnAbout;
    @FXML
    private StackPane exitAppStackPane;
    @FXML
    private StackPane minimizeAppStackPane;

    private static MainController instance = null;
    private final Logger logger = Logger.getLogger(MainController.class);
    private Button currentSelectedMenuButton;
    private List<Button> screenButtonsList;

    public static MainController getInstance() {
        if (instance == null) {
            instance = new MainController();
        }
        return instance;
    }

    @FXML
    private void initialize() {
        logger.trace("MainController.initialize");
        loadScreenInstances();
        currentSelectedMenuButton = btnRemoteScreen;
        screenButtonsList = new ArrayList<>(Arrays.asList(btnRemoteScreen, btnPingerScreen, btnAbout, btnOpenWindowScreen, btnExitApp));
        pingerScreenController.setVisible(false);
        aboutScreenController.setVisible(false);
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
        remoteScreenController.showPane();
    }

    private void loadScreenInstances() {
        KorCommon korCommon = KorCommon.getInstance();
        remoteScreenController = korCommon.getRemoteScreenController();
        pingerScreenController = korCommon.getPingerScreenController();
        aboutScreenController = korCommon.getAboutScreenController();
        remoteDriveScreenController = korCommon.getRemoteDriveScreenController();
        stackPaneAllScreens.getChildren().addAll(remoteScreenController, pingerScreenController, aboutScreenController, remoteDriveScreenController);
    }

    public void handleClicks(ActionEvent actionEvent) {
        logger.trace("ChangeScreenHandler");

        if (actionEvent.getSource() == btnRemoteScreen && currentSelectedMenuButton != btnRemoteScreen) {
            changeScreenHandler(btnRemoteScreen, remoteScreenController);
        } else if (actionEvent.getSource() == btnPingerScreen && currentSelectedMenuButton != btnPingerScreen) {
            changeScreenHandler(btnPingerScreen, pingerScreenController);
        } else if (actionEvent.getSource() == btnAbout && currentSelectedMenuButton != btnAbout) {
            changeScreenHandler(btnAbout, aboutScreenController);
        } else if (actionEvent.getSource() == btnOpenWindowScreen && currentSelectedMenuButton != btnOpenWindowScreen) {
            changeScreenHandler(btnOpenWindowScreen, remoteDriveScreenController);
        } else if (actionEvent.getSource() == btnExitApp) {
            ThreadManger.getInstance().shutDown();
            Platform.exit();
        }
    }

    private void changeScreenHandler(Button selectedMenuButton, IDisplayable selectedMenuPane) {
        this.currentSelectedMenuButton = selectedMenuButton;
        for (Button button : screenButtonsList) {
            if (button == currentSelectedMenuButton) {
                button.getStyleClass().add("selected-menu-item");
            } else {
                button.getStyleClass().remove("selected-menu-item");
            }
        }

        selectedMenuPane.showPane();
    }

    @FXML
    public void exitAppHandler() {
        exitApp();
    }

    @FXML
    public void minimizeAppHandler() {
        logger.trace("minimizeAppHandler");
        Stage stage = (Stage) minimizeAppStackPane.getScene().getWindow();
        // is stage minimizable into task bar. (true | false)
        stage.setIconified(true);
    }

    private void exitApp() {
        logger.trace("exitApp");
        ThreadManger.getInstance().shutDown();
        Platform.exit();
    }

    public RemoteScreenController getRemoteScreenController() {
        return remoteScreenController;
    }

    public PingerScreenController getPingerScreenController() {
        return pingerScreenController;
    }

    public AboutScreenController getAboutScreenController() {
        return aboutScreenController;
    }

    public RemoteDriveScreenController getRemoteDriveScreenController() {
        return remoteDriveScreenController;
    }
}
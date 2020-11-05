package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.apache.log4j.Logger;

public class MainController extends AnchorPane {

    @FXML
    private RemoteScreenController remoteScreenController;
    @FXML
    private PingerScreenController pingerScreenController;
    @FXML
    private AboutScreenController aboutScreenController;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private Button btnRemoteScreen;

    @FXML
    private Button btnExitApp;

    @FXML
    private Button btnPingerScreen;

    @FXML
    private Button btnAbout;

    private static MainController instance = new MainController();
    private final Logger logger = Logger.getLogger(MainController.class);
    private Button currentSelectedMenuButton;

    public static MainController getInstance() {
        return instance;
    }

    public void initialize() {
        logger.trace("MainController.initialize");
        currentSelectedMenuButton = btnRemoteScreen;
        pingerScreenController.setVisible(false);
        aboutScreenController.setVisible(false);
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
        remoteScreenController.showPane();
    }

    public void handleClicks(ActionEvent actionEvent) {
        logger.trace("ChangeScreenHandler");
        if (actionEvent.getSource() == btnRemoteScreen && currentSelectedMenuButton != btnRemoteScreen) {
            currentSelectedMenuButton = btnRemoteScreen;
            btnRemoteScreen.getStyleClass().add("selected-menu-item");
            btnPingerScreen.getStyleClass().remove("selected-menu-item");
            btnAbout.getStyleClass().remove("selected-menu-item");
            remoteScreenController.showPane();
        }
        if (actionEvent.getSource() == btnPingerScreen && currentSelectedMenuButton != btnPingerScreen) {
            currentSelectedMenuButton = btnPingerScreen;
            btnPingerScreen.getStyleClass().add("selected-menu-item");
            btnRemoteScreen.getStyleClass().remove("selected-menu-item");
            btnAbout.getStyleClass().remove("selected-menu-item");
            pingerScreenController.showPane();
        }

        if (actionEvent.getSource() == btnAbout && currentSelectedMenuButton != btnAbout) {
            currentSelectedMenuButton = btnAbout;
            btnAbout.getStyleClass().add("selected-menu-item");
            btnRemoteScreen.getStyleClass().remove("selected-menu-item");
            btnPingerScreen.getStyleClass().remove("selected-menu-item");
            aboutScreenController.showPane();
        }

        if (actionEvent.getSource() == btnExitApp) {
            ThreadManger.getInstance().shutDown();
            Platform.exit();
        }
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
}
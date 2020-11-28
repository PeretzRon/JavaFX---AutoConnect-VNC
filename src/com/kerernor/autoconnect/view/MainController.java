package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.screens.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainController extends AnchorPane {

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
    private StackPane exitAppStackPane;
    @FXML
    private StackPane minimizeAppStackPane;
    @FXML
    private VBox buttonScreens;
    @FXML
    StackPane koWebViewStackPane;
    @FXML
    private WebView koWebView;
    @FXML
    private StackPane stackPaneAllScreens;

    URL logoAnimatedHtml = this.getClass().getResource("/com/kerernor/autoconnect/images/ko-logo-animated.html");
    URL logoAnimatedHtmlOnlyScale = this.getClass().getResource("/com/kerernor/autoconnect/images/ko-logo-animated-scaled.html");
    private static MainController instance = null;
    private final Logger logger = Logger.getLogger(MainController.class);
    private Button currentSelectedMenuButton;
    private List<Button> screenButtonsList;

    private Button btnRemoteScreen;
    private Button btnPingerScreen;
    private Button btnOpenWindowScreen;
    private Button btnAbout;
    private Button btnExitApp;

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
        pingerScreenController.setVisible(false);
        aboutScreenController.setVisible(false);
        createAndAddMenuButtons();
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
        remoteScreenController.showPane();

        Utils.createTooltipListener(minimizeAppStackPane, Utils.MINIMIZE, KorTypes.ShowNodeFrom.RIGHT);
        Utils.createTooltipListener(exitAppStackPane, Utils.EXIT, KorTypes.ShowNodeFrom.RIGHT);

        loadKerenOrLogo();
    }

    private void loadKerenOrLogo() {
        logger.trace("loadKerenOrLogo");
        koWebView.getEngine().load(logoAnimatedHtml.toString());
        ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
            Platform.runLater(() -> {
                logger.info("Switch to logoAnimatedHtmlOnlyScale html file");
                koWebView.getEngine().load(logoAnimatedHtmlOnlyScale.toString());
            });
        }, Utils.TIME_FOR_CHANGE_LOGO_KEREN_OR_IN_SECONDS, TimeUnit.SECONDS);
    }

    private void createAndAddMenuButtons() {
        btnRemoteScreen = createButton("btnRemoteScreen", "Remote", Utils.REMOTE_ICON);
        btnPingerScreen = createButton("btnPingerScreen", "Pinger", Utils.PINGER_ICON);
        btnOpenWindowScreen = createButton("btnOpenWindowScreen", "Remote Drive", Utils.REMOTE_DRIVE_ICON);
        btnAbout = createButton("btnAbout", "About", Utils.ABOUT_ICON);
        btnExitApp = createButton("btnExitApp", "Exit", Utils.EXIT_ICON);

        addButtonsToVBox();
    }

    private void addButtonsToVBox() {
        screenButtonsList = new ArrayList<>();
        screenButtonsList.add(btnRemoteScreen);
        screenButtonsList.add(btnPingerScreen);
        if (Utils.IS_REMOTE_DRIVE_SCREEN_ACTIVE) {
            screenButtonsList.add(btnOpenWindowScreen);
        }

        screenButtonsList.add(btnAbout);
        screenButtonsList.add(btnExitApp);
        buttonScreens.getChildren().addAll(screenButtonsList);
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
            exitApp();
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

    private Button createButton(String id, String text, String iconBtn) {
        Button button = new Button();
        button.setId(id);
        button.setAlignment(Pos.BASELINE_LEFT);
        button.setGraphicTextGap(22.0);
        button.setMnemonicParsing(false);
        button.setOnAction(this::handleClicks);
        button.setPrefHeight(42.0);
        button.setPrefWidth(259);
        button.setText(text);
        button.setTextFill(Color.valueOf("#e7e5e5"));
        button.setPadding(new Insets(0, 0, 0, 50));
        ImageView imageIcon = new ImageView(Utils.getImageByName(iconBtn));
        imageIcon.setFitHeight(23);
        imageIcon.setFitWidth(27);
        imageIcon.setPickOnBounds(true);
        imageIcon.setPreserveRatio(true);
        button.setGraphic(imageIcon);
        return button;
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

    public AnchorPane getMainPane() {
        return mainPane;
    }
}
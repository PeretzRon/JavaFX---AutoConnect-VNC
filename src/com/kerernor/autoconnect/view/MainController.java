package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.screens.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainController extends AnchorPane {

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
    URL logoHtmlNoAnimation = this.getClass().getResource("/com/kerernor/autoconnect/images/ko-logo-no-animated.html");
    private static MainController instance = null;
    private final Logger logger = Logger.getLogger(MainController.class);
    private Button currentSelectedMenuButton;
    private List<Button> screenButtonsList;
    private Boolean isRunning = true;
    private boolean isShowImageWithoutAnimation = true;

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
        if (Utils.IS_CLOCK_ACTIVE) {
            createClockLabel();
        }
        btnRemoteScreen.getStyleClass().add("selected-menu-item");
        remoteScreenController.showPane();

        Utils.createTooltipListener(minimizeAppStackPane, Utils.MINIMIZE, KorTypes.ShowNodeFrom.RIGHT);
        Utils.createTooltipListener(exitAppStackPane, Utils.EXIT, KorTypes.ShowNodeFrom.RIGHT);

        loadKerenOrLogo();
    }

    private void createClockLabel() {
        Label timerLabel = new Label();
        timerLabel.setFont(Font.font(22));
        timerLabel.setTextFill(Color.valueOf("#13B4FF"));
        timerLabel.setAlignment(Pos.CENTER);
        VBox.setMargin(timerLabel, new Insets(100, 0, 0, 0));
        appClock(timerLabel);
        buttonScreens.getChildren().add(timerLabel);
    }

    private void loadKerenOrLogo() {
        logger.trace("loadKerenOrLogo");
        koWebView.getEngine().load(logoAnimatedHtml.toString());
        ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
            ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
                Thread.currentThread().setName(Utils.SWITCH_LOGO_ANIMATION_THREAD_NAME);
                while (isRunning) {
                    try {
                        if (isShowImageWithoutAnimation) {
                            if (Utils.IS_FULL_TRACE) {
                                logger.info("Switch logo from non-animated to animated");
                            }
                            Platform.runLater(() -> {
                                koWebView.getEngine().load(logoAnimatedHtmlOnlyScale.toString());
                            });
                        } else {
                            if (Utils.IS_FULL_TRACE) {
                                logger.info("Switch logo from animated to non-animated");
                            }
                            Platform.runLater(() -> {
                                koWebView.getEngine().load(logoHtmlNoAnimation.toString());
                            });
                        }
                        isShowImageWithoutAnimation = !isShowImageWithoutAnimation;
                        synchronized (isRunning) {
                            isRunning.wait(isShowImageWithoutAnimation ? 15000 : 8000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        synchronized (isRunning) {
                            isRunning = false;
                        }
                    }
                }
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

    private void appClock(Label timeLabel) {
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, event -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int sec = calendar.get(Calendar.SECOND);

            String minute = addZeroIfOneDigit(String.valueOf(min));
            String second = addZeroIfOneDigit(String.valueOf(sec));
            String time = String.format("%d:%s:%s", hour, minute, second);
            if (!timeLabel.getText().equals(time)) {
                timeLabel.setText(time);
            }
        }),
                new KeyFrame(Duration.seconds(1))
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private String addZeroIfOneDigit(String digit) {
        if (digit.length() == 1) {
            digit = "0" + digit;
        }
        return digit;
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
        LogManager.shutdown();
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
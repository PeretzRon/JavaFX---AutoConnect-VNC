package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastRemoteDriveData;
import com.kerernor.autoconnect.model.LastRemoteDriveItem;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.LastRemoteDriveListController;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class RemoteDriveScreenController extends Pane implements IDisplayable {

    @FXML
    private Pane pnlOpenWindow;
    @FXML
    private TextField ipTextFieldForRemoteWindow;
    @FXML
    private Button cancelOpenRemoteWindowBtn;
    @FXML
    private Button openRemoteWindowBtn;
    @FXML
    private ProgressBar processLoadingProgressBar;
    @FXML
    private LastRemoteDriveListController lastRemoteDriveListController;
    @FXML
    private Label noValueInListLabel;

    private Logger logger = LogManager.getLogger(RemoteDriveScreenController.class);
    private static RemoteDriveScreenController instance = null;
    private static final String DRIVE = "c$";
    Process windowsProcess = null;
    private static final BooleanProperty isProcessRunning = new SimpleBooleanProperty(false);

    public static RemoteDriveScreenController getInstance() {
        if (instance == null) {
            instance = new RemoteDriveScreenController();
        }
        return instance;
    }

    private RemoteDriveScreenController() {
        loadView();
    }

    @FXML
    public void initialize() {
        processLoadingProgressBar.setVisible(false);
        openRemoteWindowBtn.disableProperty().bind(isProcessRunning);
        cancelOpenRemoteWindowBtn.disableProperty().bind(Bindings.not(isProcessRunning));
        lastRemoteDriveListController.loadList();
        noResultLabelInitAndAddListener();

    }

    private Pane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.REMOTE_DRIVE_SCREEN));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void noResultLabelInitAndAddListener() {
        if (LastRemoteDriveData.getInstance().getLastRemoteDriveItemsList().size() == 0) {
            noValueInListLabel.toFront();
        } else {
            noValueInListLabel.toBack();
        }

        LastRemoteDriveData.getInstance().getLastRemoteDriveItemsList().addListener((ListChangeListener<? super LastRemoteDriveItem>) c -> {
            if (c.getList().size() == 0) {
                noValueInListLabel.toFront();
            } else {
                noValueInListLabel.toBack();
            }
        });
    }

    @FXML
    public void openRemoteWindowBtnHandler() {
        logger.trace("openRemoteWindowBtnHandler");
        String ip = ipTextFieldForRemoteWindow.getText();
        if (!isIPCorrectToConnect(ip)) return; // ip not valid can't continue with the operation
        String pathToConnect = createPathFromIP(ip);
        openRemoteWindowBtnInternal(pathToConnect, ip);
    }

    private String createPathFromIP(String ip) {
        return String.format("\\\\%s\\%s", ip, DRIVE);
    }

    private boolean isIPCorrectToConnect(String ip) {
        if (Utils.isValidateIpAddress(ip)) {
            return true;
        } else {
            AlertPopupController alertPopupController = new AlertPopupController();
            alertPopupController.showAlert(KorTypes.AlertTypes.WARNING, Utils.WRONG_IP_ADDRESS_MASSAGE, pnlOpenWindow);
            logger.error("can't open remote drive - ip isn't valid");
            return false;
        }
    }

    public void openRemoteWindowBtnInternal(String pathToConnect, String ip) {
        isProcessRunning.set(true);
        processLoadingProgressBar.setVisible(true);
        ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            try {
                logger.trace("openRemoteWindowBtnInternal - " + pathToConnect);
                logger.info("try connect to path: " + pathToConnect);
                Platform.runLater(() -> LastRemoteDriveData.getInstance().addItemIfNotExist(new LastRemoteDriveItem(ip, pathToConnect)));
                windowsProcess = new ProcessBuilder("explorer.exe", pathToConnect).start();
                Instant startActionTimeInstant = Instant.now();
                while (windowsProcess.isAlive()) {
                    if (Duration.between(startActionTimeInstant, Instant.now()).getSeconds() > Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS) {
                        logger.error("Abort Operation - timeout of " + Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS + " seconds");
                        windowsProcess.destroy();
                    }
                }

                logger.info("Process exit value: " + windowsProcess.exitValue());

            } catch (IOException e) {
                logger.error("unable to open window", e);
            } finally {
                Platform.runLater(() -> processLoadingProgressBar.setVisible(false));
                isProcessRunning.set(false);
            }
        });
    }

    @FXML
    public void cancelOpenRemoteWindowBtnHandler() {
        logger.trace("cancelOpenRemoteWindowBtnHandler");
        if (windowsProcess != null && windowsProcess.isAlive()) {
            windowsProcess.destroy();
            logger.info("openRemoteWindow - operation canceled by user");
        }
    }

    @Override
    public void showPane() {
        this.setVisible(true);
        this.setStyle("-fx-background-color : #02050A");
        this.toFront();
        logger.trace("showPane");
    }

    public static BooleanProperty isProcessRunningProperty() {
        return isProcessRunning;
    }
}

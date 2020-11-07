package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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

    private Logger logger = Logger.getLogger(RemoteDriveScreenController.class);
    private static final String DRIVE = "C$";
    Process windowsProcess = null;
    private final BooleanProperty isProcessRunning = new SimpleBooleanProperty(false);

    public RemoteDriveScreenController() {
        loadView();
    }

    @FXML
    public void initialize() {
        processLoadingProgressBar.setVisible(false);
        openRemoteWindowBtn.disableProperty().bind(isProcessRunning);
        cancelOpenRemoteWindowBtn.disableProperty().bind(Bindings.not(isProcessRunning));
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

    @FXML
    public void openRemoteWindowBtnHandler() {
        logger.trace("openRemoteWindowBtnHandler");
        ThreadManger.getInstance().getThreadPoolExecutor().execute(this::openRemoteWindowBtnInternal);
    }

    private void openRemoteWindowBtnInternal() {
        isProcessRunning.set(true);
        String ip = ipTextFieldForRemoteWindow.getText();

        try {
            if (Utils.isValidateIpAddress(ip)) {
                logger.trace("openRemoteWindowBtnInternal - " + ip);
                processLoadingProgressBar.setVisible(true);
                String pathToConnect = String.format("\\\\%s\\%s", ip, DRIVE);
                logger.info("try connect to path: " + pathToConnect);
                windowsProcess = new ProcessBuilder("explorer.exe", pathToConnect).start();
                Instant startActionTimeInstant = Instant.now();
                while (windowsProcess.isAlive()) {
                    if (Duration.between(startActionTimeInstant, Instant.now()).getSeconds() > Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS) {
                        logger.error("Abort Operation - timeout of " + Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS + " seconds");
                        windowsProcess.destroy();
                        return;
                    }
                }

                logger.info("Process exit value: " + windowsProcess.exitValue());

            } else {
                // alert user
                logger.error("can't open remote drive - ip isn't valid");
                Platform.runLater(() -> KorCommon.getInstance().getAlertPopupController().show(KorTypes.AlertTypes.WARNING, Utils.WRONG_IP_ADDRESS_MASSAGE, pnlOpenWindow));
            }
        } catch (IOException e) {
            logger.error("unable to open window", e);
        } finally {
            Platform.runLater(() -> processLoadingProgressBar.setVisible(false));
            isProcessRunning.set(false);
        }
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
}

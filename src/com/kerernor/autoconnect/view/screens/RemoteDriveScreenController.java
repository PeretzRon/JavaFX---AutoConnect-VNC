package com.kerernor.autoconnect.view.screens;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.ThreadManger;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

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

    private Logger logger = Logger.getLogger(RemoteDriveScreenController.class);
    private static final String DRIVE = "C$";
    Process windowsProcess = null;

    public RemoteDriveScreenController() {
        loadView();
    }

    @FXML
    public void initialize() {

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
        processLoadingProgressBar.setVisible(true);
        ThreadManger.getInstance().getThreadPoolExecutor().execute(this::openRemoteWindowBtnInternal);
    }

    private void openRemoteWindowBtnInternal() {
        String ip = ipTextFieldForRemoteWindow.getText();

        try {
            if (Utils.isValidateIpAddress(ip)) {
                processLoadingProgressBar.setVisible(true);
                Instant startActionTimeInstant = Instant.now();
                windowsProcess = new ProcessBuilder("explorer.exe", String.format("\\\\%s\\%s", ip, DRIVE)).start();
                while (windowsProcess.isAlive()) {
                    if (Duration.between(startActionTimeInstant, Instant.now()).getSeconds() > Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS) {
                        logger.error("Abort Operation - timeout of " + Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS + " seconds");
                        windowsProcess.destroy();
                    }
                }

                if (windowsProcess.exitValue() == 1) {
                    windowsProcess.destroy();
                }
            } else {
                // alert user
            }
        } catch (IOException e) {
            logger.error("unable to open window", e);
        } finally {
            Platform.runLater(() -> processLoadingProgressBar.setVisible(false));
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

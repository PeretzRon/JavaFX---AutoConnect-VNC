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
import java.util.ArrayList;
import java.util.List;
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
    //    Process windowsProcess = null;
    private AtomicInteger activeProcessCount = new AtomicInteger(0);
    private List<Process> processList = new ArrayList<>();

    public RemoteDriveScreenController() {
        loadView();
    }

    @FXML
    public void initialize() {
        processLoadingProgressBar.setVisible(false);
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
        String ip = ipTextFieldForRemoteWindow.getText();

        try {
            if (Utils.isValidateIpAddress(ip)) {
                logger.trace("openRemoteWindowBtnHandler - " + ip);
                processLoadingProgressBar.setVisible(true);
                Process windowsProcess;
                Instant startActionTimeInstant;
                synchronized (this) {
                    activeProcessCount.getAndIncrement();
                    System.out.println(activeProcessCount.get());
                    startActionTimeInstant = Instant.now();
                    windowsProcess = new ProcessBuilder("explorer.exe", String.format("\\\\%s\\%s", ip, DRIVE)).start();
                    processList.add(windowsProcess);
                }
                while (windowsProcess.isAlive()) {
                    if (Duration.between(startActionTimeInstant, Instant.now()).getSeconds() > Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS) {
                        logger.error("Abort Operation - timeout of " + Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS + " seconds");
                        cancelProcess(windowsProcess);
                        return;
                    }
                }

                if (windowsProcess.exitValue() == 0) {
                    // process finish successfully
                    cleanProcess(windowsProcess);
                }

            } else {
                // alert user
            }
        } catch (IOException e) {
            logger.error("unable to open window", e);
        }
    }

    @FXML
    public void cancelOpenRemoteWindowBtnHandler() {
        logger.trace("cancelOpenRemoteWindowBtnHandler");
        if (!processList.isEmpty()) {
            Process windowsProcess = processList.get(processList.size() - 1);
            if (windowsProcess != null && windowsProcess.isAlive()) {
                cancelProcess(windowsProcess);
                logger.info("openRemoteWindow - operation canceled by user");
            }
        } else {
            logger.trace("cancelOpenRemoteWindowBtnHandler - no process to cancel");
            if (activeProcessCount.get() <= 0 && processLoadingProgressBar.isVisible()) {
                processLoadingProgressBar.setVisible(false);
                activeProcessCount.set(0);
            }
        }

    }

    private void cleanProcess(Process process) {
        activeProcessCount.getAndDecrement();
        System.out.println("cleanProcess " + activeProcessCount.get());
        processList.remove(process);
        if (activeProcessCount.get() == 0) {
            Platform.runLater(() -> processLoadingProgressBar.setVisible(false));
        }
    }

    private void cancelProcess(Process process) {
        activeProcessCount.getAndDecrement();
        logger.trace("cancelProcess " + activeProcessCount.get());
        process.destroy();
        processList.remove(process);
        if (activeProcessCount.get() == 0) {
            Platform.runLater(() -> processLoadingProgressBar.setVisible(false));
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

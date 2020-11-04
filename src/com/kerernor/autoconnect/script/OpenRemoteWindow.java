package com.kerernor.autoconnect.script;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class OpenRemoteWindow {

    private static final String DRIVE = "C$";
    private static final Logger logger = Logger.getLogger(OpenRemoteWindow.class);

    public static void openRemoteWindow(String ip) {
        try {
            logger.trace("openRemoteWindow");
            ProgressBar progressBar = KorCommon.getInstance().getMainController().getProcessLoadingProgressBar();
            Button cancelButton = KorCommon.getInstance().getMainController().getCancelOpenRemoteWindowBtn();

            Instant startActionTimeInstant = Instant.now();
            Process p = new ProcessBuilder("explorer.exe", String.format("\\\\%s\\%s", ip, DRIVE)).start();

            EventHandler<MouseEvent> clickEvent = event -> {
                if (p.isAlive()) {
                    p.destroy();
                    logger.info("openRemoteWindow - operation canceled");
                }
            };

            cancelButton.setOnMouseClicked(clickEvent);

            while (p.isAlive()) {
                if (Duration.between(startActionTimeInstant, Instant.now()).getSeconds() > Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS) {
                    logger.error("Abort Operation - timeout of " + Utils.TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS + " seconds");
                    p.destroy();
                }
            }

            cancelButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
            if (p.exitValue() == 1) {
                p.destroy();
            }
            Platform.runLater(() -> progressBar.setVisible(false));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

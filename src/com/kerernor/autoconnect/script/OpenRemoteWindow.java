package com.kerernor.autoconnect.script;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OpenRemoteWindow {

    private static final String DRIVE = "C$";
    private static Logger logger = Logger.getLogger(OpenRemoteWindow.class);

    public static void openRemoteWindow(String ip) {
        try {
            logger.trace("openRemoteWindow");
            ProgressBar progressBar = KorCommon.getInstance().getMainController().getProcessLoadingProgressBar();

            Process p = new ProcessBuilder("explorer.exe", String.format("\\\\%s\\%s", ip, DRIVE)).start();
            try {
                p.waitFor(Utils.TIMEOUT_FOR_PROCESS_TO_END_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("openRemoteWindow timeout", e);
            }

            Platform.runLater(() -> progressBar.setProgress(0));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

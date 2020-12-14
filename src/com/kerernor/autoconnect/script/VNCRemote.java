package com.kerernor.autoconnect.script;

import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;

public class VNCRemote {

    private static final String mPathToVNC = Utils.VNC_PROGRAM_PATH;
    private static final String mPathScript = Utils.VNC_SCRIPT_PATH;
    private static final String mAdminPassword = "P@ssw0rd";
    private static Logger logger = LogManager.getLogger(VNCRemote.class);

    public static void connect(String ip, boolean isViewOnlySelected) {
        logger.info("Try to connect via VNC to: " + ip + " ViewOnly: " + isViewOnlySelected);
        try {
            FileWriter myWriter = new FileWriter(mPathScript + "run.bat");
            String isViewOnly = isViewOnlySelected ? "-viewonly" : "";
            String dataToWrite = String.format("cd %s\n" +
                    "vncviewer.exe  -connect %s  %s -password %s", mPathToVNC, isViewOnly, ip, mAdminPassword);

            myWriter.write(dataToWrite);
            myWriter.close();
            Process process = Runtime.getRuntime().exec(
                    "cmd /c run.bat", null, new File(mPathScript));

            int exitValue = process.waitFor();
            if (exitValue != 0) {
                logger.info("Process connect to vnc finished with exitValue: " + exitValue);
                sendAlert(Utils.VNC_PATH_ERROR);
                return;
            }
            logger.info("connected to: " + ip + " ViewOnly: " + isViewOnlySelected);
        } catch (Exception e1) {
            logger.error(e1);
            sendAlert(Utils.VNC_PATH_ERROR_2);
        }
    }

    public static void sendAlert(String msg) {
        Platform.runLater(() -> {
            AlertPopupController alertPopupController = new AlertPopupController();
            alertPopupController.showAlert(KorTypes.AlertTypes.ERROR, msg, KorCommon.getInstance().getMainController().getMainPane());
        });
    }
}
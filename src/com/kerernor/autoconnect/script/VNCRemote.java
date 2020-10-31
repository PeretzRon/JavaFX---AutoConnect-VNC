package com.kerernor.autoconnect.script;

import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.scene.Parent;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;

public class VNCRemote {

    private static final String mPathToVNC = Utils.VNC_PROGRAM_PATH;
    private static final String mPathScript = Utils.VNC_SCRIPT_PATH;
    private static final String mAdminPassword = "P@ssw0rd";
    private static Logger logger = Logger.getLogger(VNCRemote.class);

    public static void connect(String ip, Parent parent, boolean isViewOnlySelected) {
        logger.info("Try to connect via VNC to: " + ip + " ViewOnly: " + isViewOnlySelected);
        try {
            FileWriter myWriter = new FileWriter(mPathScript + "run.bat");
            String isViewOnly = isViewOnlySelected ? "-viewonly" : "";
            String dataToWrite = String.format("cd %s\n" +
                    "vncviewer.exe  -connect %s  %s -password %s", mPathToVNC, isViewOnly, ip, mAdminPassword);

            myWriter.write(dataToWrite);
            myWriter.close();
            Runtime.getRuntime().exec(
                    "cmd /c run.bat", null, new File(mPathScript));
            logger.info("connected to: " + ip + " ViewOnly: " + isViewOnlySelected);
        } catch (Exception e1) {
            e1.printStackTrace();
            logger.error(e1);
        }
    }
}
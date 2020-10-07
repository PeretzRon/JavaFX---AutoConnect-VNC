package com.kerernor.autoconnect.script;
import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.scene.Parent;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;

public class VNCRemote {

    private static final String mPathToVNC = "C:\\Program Files (x86)\\uvnc bvba\\UltraVNC";
    private static final String mPathScript = "C:\\Script\\run.bat";
    private static final String mAdminPassword = "P@ssw0rd";
    private static Logger logger = Logger.getLogger(VNCRemote.class);

    private static boolean validateIpAddress(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public static void connect(String ip, Parent parent, boolean isViewOnlySelected) {
        logger.info("Try to connect via VNC to: " + ip + " ViewOnly: " + isViewOnlySelected);
        if (validateIpAddress(ip)) {
            try {
                FileWriter myWriter = new FileWriter(mPathScript);
                String isViewOnly = isViewOnlySelected ? "-viewonly" : "";
                String dataToWrite = String.format("cd %s\n" +
                        "vncviewer.exe  -connect %s  %s -password %s", mPathToVNC, isViewOnly, ip, mAdminPassword);

                myWriter.write(dataToWrite);
                myWriter.close();
                Runtime.getRuntime().exec(
                        "cmd /c run.bat", null, new File("C:\\Script\\"));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } else {
            logger.info("Wrong ip address: " + ip + " Can't to connect to client");
            AlertPopupController alertPopupController = new AlertPopupController(parent);
            alertPopupController.show();
        }
    }
}
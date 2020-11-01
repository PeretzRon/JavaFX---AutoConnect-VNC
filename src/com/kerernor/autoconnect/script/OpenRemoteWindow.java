package com.kerernor.autoconnect.script;

import org.apache.log4j.Logger;

import java.io.IOException;

public class OpenRemoteWindow {

    private static final String DRIVE = "C$";
    private static Logger logger = Logger.getLogger(OpenRemoteWindow.class);

    public static void openRemoteWindow(String ip) {
        try {
            logger.trace("openRemoteWindow");
            Process p = new ProcessBuilder("explorer.exe", String.format("\\\\%s\\%s", ip, DRIVE)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

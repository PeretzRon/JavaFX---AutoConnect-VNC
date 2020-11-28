package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteDriveScreenController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;
import org.apache.log4j.Logger;

public class KorCommon {

    private static KorCommon instance = null;
    private MainController mainController = null;
    private static Logger logger = Logger.getLogger(KorCommon.class);
    private final RemoteScreenController remoteScreenController = RemoteScreenController.getInstance();
    private final PingerScreenController pingerScreenController = PingerScreenController.getInstance();
    private final AboutScreenController aboutScreenController = AboutScreenController.getInstance();
    private final RemoteDriveScreenController remoteDriveScreenController = RemoteDriveScreenController.getInstance();
    private final MonitoringUtility monitoringUtility = MonitoringUtility.getInstance();

    public  static KorCommon getInstance() {
        if (instance == null) {
            logger.trace("KorCommon init");
            instance = new KorCommon();
        }

        return instance;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public RemoteScreenController getRemoteScreenController() {
        return remoteScreenController;
    }

    public PingerScreenController getPingerScreenController() {
        return pingerScreenController;
    }

    public AboutScreenController getAboutScreenController() {
        return aboutScreenController;
    }

    public RemoteDriveScreenController getRemoteDriveScreenController() {
        return remoteDriveScreenController;
    }

    public MonitoringUtility getMonitoringUtility() {
        return monitoringUtility;
    }
}

package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.popups.AlertPopupController;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteDriveScreenController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;

public class KorCommon {

    private static KorCommon instance = null;
    private MainController mainController = null;
    private final RemoteScreenController remoteScreenController = RemoteScreenController.getInstance();
    private final PingerScreenController pingerScreenController = PingerScreenController.getInstance();
    private final AboutScreenController aboutScreenController = AboutScreenController.getInstance();
    private final RemoteDriveScreenController remoteDriveScreenController = RemoteDriveScreenController.getInstance();

    public static KorCommon getInstance() {
        if (instance == null) {
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
}

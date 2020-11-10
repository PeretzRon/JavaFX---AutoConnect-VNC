package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.popups.AlertPopupController;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteDriveScreenController;

public class KorCommon {

    private static KorCommon instance = null;
    private MainController mainController = null;
    private final AboutScreenController aboutScreenController = null;
    private final PingerScreenController pingerScreenController = null;
    private final RemoteDriveScreenController remoteDriveScreenController = null;
    private final AlertPopupController alertPopupController = AlertPopupController.getInstance();

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

    public AboutScreenController getAboutScreenController() {
        return aboutScreenController;
    }

    public PingerScreenController getPingerScreenController() {
        return pingerScreenController;
    }

    public AlertPopupController getAlertPopupController() {
        return alertPopupController;
    }

    public RemoteDriveScreenController getRemoteDriveScreenController() {
        return remoteDriveScreenController;
    }
}

package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import com.kerernor.autoconnect.view.screens.AboutScreenController;
import com.kerernor.autoconnect.view.screens.PingerScreenController;
import com.kerernor.autoconnect.view.screens.RemoteDriveScreenController;

public class KorCommon {

    private static KorCommon instance = null;
    private MainController mainController = null;
    private AboutScreenController aboutScreenController = null;
    private PingerScreenController pingerScreenController = null;
    private RemoteDriveScreenController remoteDriveScreenController = null;
    private AlertPopupController alertPopupController = AlertPopupController.getInstance();

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

    public void setAboutScreenController(AboutScreenController aboutScreenController) {
        this.aboutScreenController = aboutScreenController;
    }

    public PingerScreenController getPingerScreenController() {
        return pingerScreenController;
    }

    public void setPingerScreenController(PingerScreenController pingerScreenController) {
        this.pingerScreenController = pingerScreenController;
    }

    public RemoteDriveScreenController getRemoteDriveScreenController() {
        return remoteDriveScreenController;
    }

    public void setRemoteDriveScreenController(RemoteDriveScreenController remoteDriveScreenController) {
        this.remoteDriveScreenController = remoteDriveScreenController;
    }

    public AlertPopupController getAlertPopupController() {
        return alertPopupController;
    }

    public void setAlertPopupController(AlertPopupController alertPopupController) {
        this.alertPopupController = alertPopupController;
    }

}

package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.AboutScreenController;
import com.kerernor.autoconnect.view.MainController;
import com.kerernor.autoconnect.view.popups.AlertPopupController;

public class KorCommon {

    private static KorCommon instance = null;
    private MainController mainController = null;
    private AboutScreenController aboutScreenController = null;

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
}

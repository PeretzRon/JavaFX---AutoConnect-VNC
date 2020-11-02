package com.kerernor.autoconnect.util;

import com.kerernor.autoconnect.view.MainController;

public class KorCommon {

    static KorCommon instance = null;
    MainController mainController = null;

    private KorCommon() {

    }

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
}

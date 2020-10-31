package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class LastConnectionsPopupController extends GridPane {

    @FXML
    private LastConnectionListController lastConnectionListController;

    @FXML
    private Button bbb;

    public LastConnectionsPopupController() {

    }

    @FXML
    public void initialize() {
        lastConnectionListController.loadList();
    }

    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_CONNECTION_POPUP_VIEW));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    public void show() {
        loadView();
    }
}

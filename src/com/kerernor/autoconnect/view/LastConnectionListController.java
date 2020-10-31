package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastConnectionData;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;

import java.io.IOException;

public class LastConnectionListController extends ListView {

    @FXML
    private ListView<LastConnectionItem> lastConnectionList;

    public LastConnectionListController() {
        super();
        loadView();
//        lastConnectionList.
    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_CONNECTION_LIST_VIEW));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadList() {
        LastConnectionData.getInstance().getLastConnectionItems().add(new LastConnectionItem("121212"));
        lastConnectionList.setItems(LastConnectionData.getInstance().getLastConnectionItems());

        lastConnectionList.setCellFactory(o -> {
            LastConnectionRowController lastConnectionRowController = new LastConnectionRowController();
            return lastConnectionRowController;
        });
    }

}

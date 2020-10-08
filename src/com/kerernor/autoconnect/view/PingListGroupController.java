package com.kerernor.autoconnect.view;


import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PingListGroupController extends ListView {

    @FXML
    private ListView<PingerItem> pingerListView;

    private Pane paneBehind;

    private PingRowItemController pingRowItemController;

    private ObservableList<PingerItem> listToSendPing;

    public ObservableList<PingerItem> getListToSendPing() {
        return listToSendPing;
    }

    public PingRowItemController getPingRowItemController() {
        return pingRowItemController;
    }

    public PingListGroupController() {
        super();
//        this.paneBehind = paneBehind;
        loadView();
    }


    public void initialize() {

    }


    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PINGER_LIST));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetProgressBar() {
        this.listToSendPing.forEach(pingerItem -> {
            pingerItem.setProgressValue(0);
        });
    }

    public void loadList(ObservableList<PingerItem> list) {
        this.listToSendPing = list;
        pingerListView.setItems(list);

        pingerListView.setCellFactory(o -> {
            pingRowItemController = new PingRowItemController();

            return pingRowItemController;
        });
    }
}

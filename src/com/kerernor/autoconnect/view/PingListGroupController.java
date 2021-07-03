package com.kerernor.autoconnect.view;


import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class PingListGroupController extends FlowPane {

    @FXML
    private FlowPane pingerListView;

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

    public FlowPane getPingerListView() {
        return pingerListView;
    }

    public void initialize() {
        pingerListView.setHgap(7);
        pingerListView.setVgap(7);
        pingerListView.setPadding(new Insets(10, 10, 10, 10));
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
        pingerListView.getChildren().clear();
        this.listToSendPing = list;
        list.forEach(pingerItem -> {
            pingRowItemController = new PingRowItemController(pingerItem);
            pingerListView.getChildren().add(pingRowItemController);

        });
    }

}

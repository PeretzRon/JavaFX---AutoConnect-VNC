package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


import java.io.IOException;

public class PingGroupItemController extends HBox {

    @FXML
    private HBox mainPane;

    @FXML
    private Label name;

    @FXML
    private ImageView editItem;

    @FXML
    private ImageView deleteItem;

    private final Pinger pingerItem;

    public PingGroupItemController(Pinger pingerItem) {
        this.pingerItem = pingerItem;
        loadView();
        setDataToComponent();
    }

    private void setDataToComponent() {
        name.setText(pingerItem.getName());
    }

    @FXML
    public void initialize() {
        mainPane.setOnMouseClicked(event -> {

        });
    }

    private HBox loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PING_GROUP_ITEM));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public HBox getMainPane() {
        return mainPane;
    }

    public Label getName() {
        return name;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}

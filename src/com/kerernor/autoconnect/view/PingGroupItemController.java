package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    private Parent behindPane;

    public PingGroupItemController(Pinger pingerItem, Parent behindPane) {
        this.pingerItem = pingerItem;
        this.behindPane = behindPane;
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

    @FXML
    public void deletePingGroupHandler(MouseEvent event) {
        event.consume();
        ConfirmPopupController confirmPopupController = new ConfirmPopupController(behindPane, null, pingerItem);
        confirmPopupController.openPopup();
    }

    @FXML
    public void editPingGroupHandler(MouseEvent event) {
        event.consume();
        AddEditPingerItemsController addEditPingerItemsController = new AddEditPingerItemsController(behindPane, pingerItem, true);
        addEditPingerItemsController.show();
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

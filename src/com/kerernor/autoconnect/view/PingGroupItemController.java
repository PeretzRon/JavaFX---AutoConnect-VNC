package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;


import java.io.IOException;

public class PingGroupItemController extends HBox {

    //TODO: add logger

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

    private EventHandler<KorEvents.PingerEvent> infoEditPingerItems;
    private EventHandler<KorEvents.PingerEvent> infoEditPingerGroupName;
    private EventHandler<KorEvents.PingerEvent> infoEditPingerExit;

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
    public void deletePingGroupHandler() {
        ConfirmPopupController confirmPopupController = ConfirmPopupController.getInstance();
        confirmPopupController.setConfiguration(behindPane, pingerItem);
        KorTypes.ConfirmPopUpControllerTypes callback = confirmPopupController.openPopup();
        switch (callback) {
            case CONFIRM:
                PingerData.getInstance().remove(pingerItem);
                break;
            case EXIT:
            default:
                break;
        }
    }

    @FXML
    public void editPingGroupHandler() {
        AddEditPingerItemsController addEditPingerItemsController = AddEditPingerItemsController.getInstance();
        addEditPingerItemsController.setConfiguration(behindPane, pingerItem, true);

        infoEditPingerItems = event -> {
            event.consume();
            fireEvent(event);
            removeEventsHandler();
        };

        infoEditPingerGroupName = event -> {
            event.consume();
            fireEvent(event);
            removeEventsHandler();
        };

        infoEditPingerExit = event -> {
            event.consume();
            removeEventsHandler();
        };

        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, infoEditPingerGroupName);
        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, infoEditPingerItems);
        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.EXIT, infoEditPingerExit);

        addEditPingerItemsController.show();
    }

    private void removeEventsHandler() {
        AddEditPingerItemsController addEditPingerItemsController = AddEditPingerItemsController.getInstance();
        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, infoEditPingerGroupName);
        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, infoEditPingerItems);
        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.EXIT, infoEditPingerExit);
        infoEditPingerGroupName = infoEditPingerExit = infoEditPingerItems = null;
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

package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;
import com.kerernor.autoconnect.view.popups.AddEditPingerItemsController;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

public class PingGroupItemController extends HBox {

    //TODO: add logger

    @FXML
    private HBox mainPane;

    @FXML
    private JSearchableTextFlowController name;

    @FXML
    private ImageView editItem;

    @FXML
    private ImageView deleteItem;

    private final Pinger pingerItem;
    private final Parent behindPane;

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
        name.initText(pingerItem.getName(), true);
        name.setFont(Font.font(16));
        name.setAlignment(TextAlignment.CENTER);
    }

    @FXML
    public void initialize() {
         // TODO: check why need platform , otherwise tha app stuck on startup
        Platform.runLater(() -> KorCommon.getInstance().getPingerScreenController().getActiveSearchableTextFlowMap().add(name));
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
        AddEditPingerItemsController addEditPingerItemsController = new AddEditPingerItemsController(behindPane, pingerItem, true);

        infoEditPingerItems = event -> {
            event.consume();
            fireEvent(event);
//            removeEventsHandler();
        };

        infoEditPingerGroupName = event -> {
            event.consume();
            fireEvent(event);
//            removeEventsHandler();
        };

        infoEditPingerExit = event -> {
            event.consume();
//            removeEventsHandler();
        };

        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, infoEditPingerGroupName);
        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, infoEditPingerItems);
        addEditPingerItemsController.addEventHandler(KorEvents.PingerEvent.EXIT, infoEditPingerExit);

        addEditPingerItemsController.show();
    }

//    private void removeEventsHandler() {
//        AddEditPingerItemsController addEditPingerItemsController = new  AddEditPingerItemsController();
//        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_NAME, infoEditPingerGroupName);
//        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.UPDATE_PINGER_ITEM, infoEditPingerItems);
//        addEditPingerItemsController.removeEventHandler(KorEvents.PingerEvent.EXIT, infoEditPingerExit);
//        infoEditPingerGroupName = infoEditPingerExit = infoEditPingerItems = null;
//    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PING_GROUP_ITEM));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public HBox getMainPane() {
        return mainPane;
    }

    public String getName() {
        return name.getOriginalText();
    }

    public Pinger getPingerItem() {
        return pingerItem;
    }

    @Override
    public String toString() {
        return name.toString();
    }
}

package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddEditPingerItemsController {

    @FXML
    private GridPane mainPane;

    @FXML
    private TextField groupNameTextField;

    @FXML
    private TextField IPTextField;

    @FXML
    private TextField nameItemTextField;

    @FXML
    private ImageView addIPImageView;

    @FXML
    private ListView<String> addedItemsList;

    @FXML
    private Label addEditPingItemTitle;

    private Stage stage;
    private Parent paneBehind;

    private ObservableList<String> pingerItemsAddedObservableList;
    private List<PingerItem> pingerItemList;

    private Pinger pingerItem;
    private boolean isEditItem;

    public AddEditPingerItemsController(Parent paneBehind, Pinger pingerItem, boolean isEditItem) {
        this.paneBehind = paneBehind;
        this.pingerItem = pingerItem;
        this.isEditItem = isEditItem;
    }


    public GridPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.ADD_EDIT_PINGER_ITEMS));
        loader.setController(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @FXML
    public void initialize() {
        pingerItemsAddedObservableList = FXCollections.observableArrayList();
        pingerItemList = new ArrayList<>();
        addedItemsList.setItems(pingerItemsAddedObservableList);
        Platform.runLater(() ->  mainPane.requestFocus());
    }

    @FXML
    public void addPingItemToListHandler() {
        String ip = IPTextField.getText();
        String name = nameItemTextField.getText();
        pingerItemList.add(new PingerItem(ip, name));
        pingerItemsAddedObservableList.add(displayItemNameInList(name, ip));
    }

    @FXML
    public void saveClickAction() {
        String groupName = groupNameTextField.getText();

        if (pingerItem == null) {
            pingerItem = new Pinger(groupName, pingerItemList);
            PingerData.getInstance().add(pingerItem);
        } else {
            pingerItem.setName(groupName);
            pingerItem.setData(pingerItemList);
            //TODO: refreshList
            //TODO: scroll color
        }

        closeClickAction();
    }

    public void show() {
        Scene scene = new Scene(this.loadView());

        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);


        // Set out of focus closing ability
        stage.focusedProperty().addListener((observableValue, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                closeClickAction();
            }
        });

        if (isEditItem) {
            addEditPingItemTitle.setText(Utils.TEXT_EDIT_COMPUTER_POPUP + pingerItem.getName());
            groupNameTextField.setText(pingerItem.getName());
            pingerItemList.addAll(pingerItem.getData());
            pingerItemList.forEach(item -> {
                pingerItemsAddedObservableList.add(displayItemNameInList(item.getName(), item.getIpAddress()));
            });
        } else {
            addEditPingItemTitle.setText(Utils.TEXT_ADD_NEW_GROUP_PINGER_POPUP_TITTLE);
        }

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());
        stage.show();

        // center stage
        Utils.centerNewStageToBehindStage(paneBehind, stage);

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());
    }

    public void closeClickAction() {
        // Revert the blur effect from the pane behind
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());

        stage.close();
    }

    private String displayItemNameInList(String name, String ip) {
        return name + " - " + ip;
    }
}

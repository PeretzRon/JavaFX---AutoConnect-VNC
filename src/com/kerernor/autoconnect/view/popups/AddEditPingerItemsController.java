package com.kerernor.autoconnect.view.popups;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class AddEditPingerItemsController {

    @FXML
    private TextField groupNameTextField;

    @FXML
    private TextField IPTextField;

    @FXML
    private ImageView addIPImageView;

    @FXML
    ListView<String> addedItemsList;

    private Stage stage;
    private Parent paneBehind;

    private ObservableList<String> pingerItemsAddedList;

    public AddEditPingerItemsController(Parent paneBehind) {
        this.paneBehind = paneBehind;
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
        pingerItemsAddedList = FXCollections.observableArrayList();
        addedItemsList.setItems(pingerItemsAddedList);
    }

    @FXML
    public void addPingItemToListHandler() {
        String ip = IPTextField.getText();
        pingerItemsAddedList.add(ip);
    }

    @FXML
    public void saveClickAction() {
        String groupName = groupNameTextField.getText();
        Pinger pinger = new Pinger();
        pinger.setName(groupName);
//        pinger.setData(pingerItemsAddedList);
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
//                closeClickAction();
            }
        });

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.NONE);
        stage.initOwner(paneBehind.getScene().getWindow());
        stage.show();

        // center stage
        Utils.centerNewStageToBehindStage(paneBehind, stage);

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());
    }
}

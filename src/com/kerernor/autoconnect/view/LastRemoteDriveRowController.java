package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.LastRemoteDriveItem;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class LastRemoteDriveRowController extends ListCell<LastRemoteDriveItem> {
    @FXML
    private GridPane mainPane;
    @FXML
    private BorderPane borderPane;
    @FXML
    private Label lastIPConnection;
    @FXML
    private Button connectBtn;

    private FXMLLoader loader;
    private LastRemoteDriveItem lastRemoteDriveItem;

    public LastRemoteDriveRowController() {

    }

    @FXML
    public void initialize() {

    }

    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_REMOTE_DRIVE_ROW_VIEW));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    @Override
    protected void updateItem(LastRemoteDriveItem lastRemoteDriveItem, boolean empty) {
        super.updateItem(lastRemoteDriveItem, empty);
        if (empty || lastRemoteDriveItem == null) {
            setText(null);
            setGraphic(null);
        } else {
//            setStyle(" -fx-background-color: transparent;");
            loadAndSetValues(lastRemoteDriveItem);
            setText(null);
            setGraphic(mainPane);
        }
    }

    public void loadAndSetValues(LastRemoteDriveItem lastRemoteDriveItem) {
        this.lastRemoteDriveItem = lastRemoteDriveItem;

        if (loader == null) {
            loader = loadView();
        }

        lastIPConnection.setText(lastRemoteDriveItem.getIp());
    }
}

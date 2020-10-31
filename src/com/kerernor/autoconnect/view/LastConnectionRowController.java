package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastConnectionData;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class LastConnectionRowController extends ListCell<LastConnectionItem> {

    @FXML
    private GridPane mainPane;

    @FXML
    private ImageView deleteRowButton;

    @FXML
    private Label ipText;

    private LastConnectionItem lastConnectionItem;
    private FXMLLoader loader;

    @FXML
    public void initialize() {
        deleteRowButton.setOnMouseClicked(event -> {
            event.consume();
            LastConnectionData.getInstance().removeHistoryItem(lastConnectionItem);
        });

        mainPane.setOnMouseClicked(event -> {
            event.consume();
            fireEvent(new KorEvents.SearchHistoryConnectionEvent(KorEvents.SearchHistoryConnectionEvent.SEARCH_HISTORY_CONNECTION_EVENT_EVENT_TYPE, lastConnectionItem));
        });
    }


    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_CONNECTION_ROW_VIEW));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    @Override
    protected void updateItem(LastConnectionItem item, boolean empty) {
        super.updateItem(item, empty);
        this.lastConnectionItem = item;
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            loadAndSetValues(item);
            setText(null);
            setGraphic(mainPane);
        }
    }

    private void loadAndSetValues(LastConnectionItem item) {
        this.lastConnectionItem = item;

        if (loader == null) {
            loader = loadView();
        }

        ipText.setText(item.getIp());
    }
}

package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LastConnectionListController extends ListView {

    @FXML
    private ListView<LastConnectionItem> lastConnectionList;

    private final Logger logger = LogManager.getLogger(LastConnectionListController.class);

    public LastConnectionListController() {
        super();
        loadView();
    }

    @FXML
    public void initialize() {
        logger.debug("LastConnectionListController.initialize");
        lastConnectionList.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                LastConnectionItem selectedItem = lastConnectionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    fireEvent(new KorEvents.SearchHistoryConnectionEvent(KorEvents.SearchHistoryConnectionEvent.SEARCH_HISTORY_CONNECTION_EVENT_EVENT_TYPE, selectedItem));
                }
            }
        });
    }

    private void loadView() {
        logger.debug("LastConnectionListController.loadView");
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_CONNECTION_LIST_VIEW));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadList(FilteredList<LastConnectionItem> historySearchFilteredList) {
        logger.debug("LastConnectionListController.loadList");
        lastConnectionList.setItems(historySearchFilteredList);

        lastConnectionList.setCellFactory(o -> {
            LastConnectionRowController lastConnectionRowController = new LastConnectionRowController();
            return lastConnectionRowController;
        });
    }

    public ListView<LastConnectionItem> getLastConnectionList() {
        return lastConnectionList;
    }
}

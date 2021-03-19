package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastConnectionItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class LastConnectionsPopupController extends GridPane {

    @FXML
    private GridPane mainPane;

    @FXML
    private LastConnectionListController lastConnectionListController;

    private final Logger logger = LogManager.getLogger(LastConnectionsPopupController.class);

    public LastConnectionsPopupController() {
        loadView();
    }

    @FXML
    public void initialize() {
        mainPane.setVisible(false);
    }

    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_CONNECTION_POPUP_VIEW));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    public boolean isShow() {
        return mainPane.isVisible();
    }

    public void show() {
        logger.debug("LastConnectionsPopupController.show");
        mainPane.setVisible(true);
        lastConnectionListController.getLastConnectionList().scrollTo(0);
        lastConnectionListController.getLastConnectionList().requestFocus();
    }

    public void setList(FilteredList<LastConnectionItem> historySearchFilteredList) {
        lastConnectionListController.loadList(historySearchFilteredList);
    }

    public void hide() {
        logger.debug("LastConnectionsPopupController.hide");
        mainPane.setVisible(false);
    }

    public LastConnectionListController getLastConnectionListController() {
        return lastConnectionListController;
    }
}

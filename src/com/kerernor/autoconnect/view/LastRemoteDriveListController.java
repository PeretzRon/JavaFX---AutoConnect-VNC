package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.LastRemoteDriveData;
import com.kerernor.autoconnect.model.LastRemoteDriveItem;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.apache.log4j.Logger;

import java.io.IOException;

public class LastRemoteDriveListController extends ListView {

    @FXML
    private ListView<LastRemoteDriveItem> lastRemoteDriveList;

    private Logger logger = Logger.getLogger(LastRemoteDriveListController.class);
    private Pane paneBehind;

    public void setPaneBehind(Pane paneBehind) {
        this.paneBehind = paneBehind;
    }

    public LastRemoteDriveListController() {
        this(new Pane());
    }

    public LastRemoteDriveListController(Pane paneBehind) {
        super();
        this.paneBehind = paneBehind;
        loadView();
    }

    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.LAST_REMOTE_DRIVE_LIST));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadList() {
        logger.trace("loadList");
        lastRemoteDriveList.setItems(LastRemoteDriveData.getInstance().getLastRemoteDriveItems());

        lastRemoteDriveList.setCellFactory(lastRemoteDriveItemListView -> {
            LastRemoteDriveRowController lastRemoteDriveRowController = new LastRemoteDriveRowController();

            return lastRemoteDriveRowController;
        });
    }

    public ListView<LastRemoteDriveItem> getLastRemoteDriveList() {
        return lastRemoteDriveList;
    }
}

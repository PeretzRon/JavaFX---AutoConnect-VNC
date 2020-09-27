package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.PingerItem;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class PingRowItemController extends ListCell<PingerItem> {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label name;

    @FXML
    private Label ipAddress;

    @FXML
    private ProgressBar progressBar;

    private PingerItem pingerItem;
    private FXMLLoader loader;

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void initialize() {
        progressBar.progressProperty().addListener((obs, oldProgress, newProgress) -> {
            if (newProgress.doubleValue() > 101) {
                progressBar.setStyle("-fx-accent: green");
            } else {
                progressBar.setStyle("-fx-accent: red");
            }
        });
    }

    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PINGER_ROW_VIEW));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    @Override
    protected void updateItem(PingerItem pingerItem, boolean empty) {
        super.updateItem(pingerItem, empty);
        if (empty || pingerItem == null) {
            setText(null);
            setGraphic(null);
        } else {
            setStyle(" -fx-background-color: transparent;"); // TODO: move to css file
            loadAndSetValues(pingerItem);
            setText(null);
            setGraphic(mainPane);
            progressBar.progressProperty().bind(pingerItem.getVal());
        }
    }

    public void loadAndSetValues(PingerItem pingerItem) {
        this.pingerItem = pingerItem;
        if (loader == null) {
            loader = loadView();
        }

        name.setText(pingerItem.getName());
        ipAddress.setText(pingerItem.getIpAddress());
    }


}

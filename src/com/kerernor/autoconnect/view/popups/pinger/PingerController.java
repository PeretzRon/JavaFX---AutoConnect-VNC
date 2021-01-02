package com.kerernor.autoconnect.view.popups.pinger;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.PingGroupItemController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;

public class PingerController extends VBox {

    @FXML
    private Label groupNameLabel;

    private int indexOnGrid;
    private KorTypes.PingerGridItemState state;
    private final StringProperty firstRow = new SimpleStringProperty("");
    private PingGroupItemController pingGroupItemController;


    public PingGroupItemController getPingGroupItemController() {
        return pingGroupItemController;
    }

    public void setPingGroupItemController(PingGroupItemController pingGroupItemController) {
        this.pingGroupItemController = pingGroupItemController;
    }

    public PingerController() {
        this.state = KorTypes.PingerGridItemState.EMPTY;
        loadView();
    }

    @FXML
    public void initialize() {
        groupNameLabel.textProperty().bind(firstRow);
        groupNameLabel.setTextFill(Color.web("#05071F"));
    }

    public VBox loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PINGER_POPUP_ITEM));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFirstRow() {
        return firstRow.get();
    }

    public void setFirstRow(String firstRow) {
        this.firstRow.set(firstRow);
    }

    public int getIndexOnGrid() {
        return indexOnGrid;
    }

    public void setIndexOnGrid(int indexOnGrid) {
        this.indexOnGrid = indexOnGrid;
    }

    public KorTypes.PingerGridItemState getState() {
        return state;
    }

    public void setState(KorTypes.PingerGridItemState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "PingerController{" +
                "firstRow=" + firstRow +
                '}';
    }
}

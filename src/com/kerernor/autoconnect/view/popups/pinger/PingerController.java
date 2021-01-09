package com.kerernor.autoconnect.view.popups.pinger;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.PingGroupItemController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class PingerController extends VBox {

    @FXML
    private TextFlow groupNameTextFlow;

    private int indexOnGrid;
    private KorTypes.PingerGridItemState state;
    private final StringProperty firstRow = new SimpleStringProperty("");
    private PingGroupItemController pingGroupItemController;
    private Pinger pinger;
    private boolean isDeleted = false;


    public static PingerController emptyPingerController() {
        PingerController empty = new PingerController();
        empty.setDeleted(false);
        empty.setState(KorTypes.PingerGridItemState.EMPTY);
        empty.setIndexOnGrid(-1);
        empty.setFirstRow("");
        empty.setPinger(null);
        return empty;
    }

    @FXML
    public void initialize() {
        Text text = new Text();
        text.setFill(Color.WHITE);
        text.textProperty().bind(firstRow);
        groupNameTextFlow.getChildren().setAll(text);
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
        this.firstRow.set(Utils.addDotIfTextIsLong(firstRow, 14));
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

    public PingGroupItemController getPingGroupItemController() {
        return pingGroupItemController;
    }

    public Pinger getPinger() {
        return pinger;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public void setPinger(Pinger pinger) {
        this.pinger = pinger;
    }

    public PingerController() {
        this.state = KorTypes.PingerGridItemState.EMPTY;
        loadView();
    }

    @Override
    public String toString() {
        return "PingerController{" +
                "firstRow=" + firstRow +
                '}';
    }

}

package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.script.VNCRemote;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ComputerRowController extends ListCell<Computer> {

    @FXML
    private BorderPane mainPane;

    @FXML
    private Label computerLocation;

    @FXML
    private Label computerIP;

    @FXML
    private Label computerName;

    @FXML
    private ImageView computerType;

    @FXML
    private ImageView removeBtnID;

    private Computer computer;
    private FXMLLoader loader;
    private Parent paneBehind;

    public FXMLLoader loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.COMPUTER_ROW_VIEW));
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return loader;
    }

    public ComputerRowController(Pane paneBehind) {
        this.paneBehind = paneBehind;
    }

    @Override
    protected void updateItem(Computer computer, boolean empty) {
        super.updateItem(computer, empty);
        if (empty || computer == null) {
            setText(null);
            setGraphic(null);
        } else {
            setStyle(" -fx-background-color: transparent;"); // TODO: move to css file
            loadAndSetValues(computer);
            setText(null);
            setGraphic(mainPane);
        }
    }

    public void loadAndSetValues(Computer computer) {
        this.computer = computer;

        if (loader == null) {
            loader = loadView();
        }

        computerName.setText(computer.getName());
        computerLocation.setText(computer.getItemLocation());
        computerIP.setText(computer.getIp());
        if (computer.getComputerType() == eComputerType.RCGW) {
            computerType.setImage(new Image(Utils.RCGW_ICON));
        } else {
            computerType.setImage(new Image(Utils.STATION_ICON));
        }
    }

    @FXML
    public void connectToComputer() {
        VNCRemote.connect(computer.getIp(), mainPane);
    }

    @FXML
    public void removeComputer() {
        ConfirmPopupController confirmPopupController = new ConfirmPopupController(paneBehind, computer);
        confirmPopupController.openPopup();

    }

    public void editComputer() {
        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(paneBehind);

        // TODO: delete
        addEditComputerPopup.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
            fireEvent(event);
        });

        addEditComputerPopup.openPopup(true, computer);
    }
}

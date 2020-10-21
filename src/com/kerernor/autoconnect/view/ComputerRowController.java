package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.eComputerType;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Objects;

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
    @FXML
    private ImageView connectBtn;

    @FXML
    private Button addToDrawerButton;

    private Computer computer;
    private FXMLLoader loader;
    private Parent paneBehind;

    private DoubleProperty dragXDiff = new SimpleDoubleProperty(0);
    private double DRAWER_BUTTON_RIGHT_MARIGN;
    private double DRAWER_BUTTON_WIDTH;

    public void initialize() {
        mainPane.getLeft().translateXProperty().bind(dragXDiff);
        DRAWER_BUTTON_RIGHT_MARIGN = HBox.getMargin(addToDrawerButton).getLeft();
        DRAWER_BUTTON_WIDTH = addToDrawerButton.getPrefWidth();
        dragXDiff.setValue(DRAWER_BUTTON_RIGHT_MARIGN + DRAWER_BUTTON_WIDTH);

        mainPane.setOnMouseDragged(event -> {
            DragHandler.drag(DRAWER_BUTTON_RIGHT_MARIGN,
                    DRAWER_BUTTON_WIDTH,
                    dragXDiff, event.getSceneX(),
                    this);
        });

        mainPane.setOnMouseReleased(event -> {
            event.consume();
            if (addToDrawerButton != null) {
                Platform.runLater(() -> {
                    DragHandler.handleEndDrag(this);
                    if (DragHandler.isDrawerShown(this) && isReleaseOnAddToDrawerButton(event)) {

                    }
                });
            }

        });
    }

    private boolean isReleaseOnAddToDrawerButton(javafx.scene.input.MouseEvent event) {
        if (addToDrawerButton != null) {
            return event.getX() > mainPane.getWidth() - addToDrawerButton.getWidth();
        } else {
            return false;
        }
    }

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
//            setStyle(" -fx-background-color: transparent;");
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
            computerType.setImage(Utils.getImageByName(Utils.RCGW_ICON));
        } else {
            computerType.setImage(Utils.getImageByName(Utils.STATION_ICON));
        }
    }

    @FXML
    public void connectToComputer() {
        String ipAddress = computer.getIp();
        fireEvent(new KorEvents.ConnectVNCEvent(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, ipAddress, paneBehind));
    }

    @FXML
    public void removeComputer() {
        ConfirmPopupController confirmPopupController = new ConfirmPopupController(paneBehind, computer, null);
        confirmPopupController.openPopup();

    }

    public void editComputer() {
        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(paneBehind, true);

        // TODO: delete
        addEditComputerPopup.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
            fireEvent(event);
        });

        addEditComputerPopup.openPopup(computer);
    }
}

package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.components.JSearchableTextFlowController;
import com.kerernor.autoconnect.view.popups.AddEditComputerPopup;
import com.kerernor.autoconnect.view.popups.ConfirmPopupController;
import com.kerernor.autoconnect.view.screens.RemoteScreenController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.Set;

public class ComputerRowController extends ListCell<Computer> {

    @FXML
    private BorderPane mainPane;
    @FXML
    private JSearchableTextFlowController computerLocation;
    @FXML
    private JSearchableTextFlowController computerIP;
    @FXML
    private JSearchableTextFlowController computerName;
    @FXML
    private ImageView computerType;
    @FXML
    private ImageView removeBtnID;

    private Computer computer;
    private FXMLLoader loader;
    private Parent paneBehind;
    private Logger logger = Logger.getLogger(ComputerRowController.class);

    public void initialize() {
        computerName.setColorFoundText(Color.YELLOW);
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
            addAndExecuteStyleOnText();
        }
    }

    private void addAndExecuteStyleOnText() {
        final Set<JSearchableTextFlowController> activeSearchableTextFlowMap = KorCommon.getInstance().getRemoteScreenController().getActiveSearchableTextFlowMap();
        activeSearchableTextFlowMap.add(computerIP);
        activeSearchableTextFlowMap.add(computerName);
        activeSearchableTextFlowMap.add(computerLocation);
        final RemoteScreenController remoteScreenController = KorCommon.getInstance().getRemoteScreenController();
        final String inputFromSearch = remoteScreenController.getSearchAreaController().getTextField().getText();
        Utils.updateStyleOnText(inputFromSearch, inputFromSearch.toLowerCase(), remoteScreenController);
    }

    public void loadAndSetValues(Computer computer) {
        this.computer = computer;

        if (loader == null) {
            loader = loadView();
        }

        computerName.initText(computer.getName());
        computerName.setFont(Font.font(25));
        computerLocation.initText(computer.getItemLocation());
        computerLocation.setFont(Font.font(16));
        computerIP.initText(computer.getIp());
        computerIP.setFont(Font.font(16));
        if (computer.getComputerType() == KorTypes.ComputerType.RCGW) {
            computerType.setImage(Utils.getImageByName(Utils.RCGW_ICON));
        } else if (computer.getComputerType() == KorTypes.ComputerType.Station) {
            computerType.setImage(Utils.getImageByName(Utils.STATION_ICON));
        } else {
            computerType.setImage(Utils.getImageByName(Utils.OTHER_ICON));
        }
    }

    @FXML
    public void connectToComputer() {
        String ipAddress = computer.getIp();
        fireEvent(new KorEvents.ConnectVNCEvent(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, ipAddress, paneBehind));
    }

    @FXML
    public void removeComputer() {
        ConfirmPopupController confirmPopupController = ConfirmPopupController.getInstance();
        confirmPopupController.setConfiguration(paneBehind, computer);
        KorTypes.ConfirmPopUpControllerTypes callback = confirmPopupController.openPopup();
        switch (callback) {
            case CONFIRM:
                ComputerData.getInstance().remove(computer);
                break;
            case EXIT:
            default:
                break;
        }
    }

    public void editComputer() {
        AddEditComputerPopup addEditComputerPopup = new AddEditComputerPopup(paneBehind, true);

        // TODO: delete
        addEditComputerPopup.addEventHandler(KorEvents.SearchComputerEvent.SEARCH_COMPUTER_EVENT, event -> {
            fireEvent(event);
        });

        addEditComputerPopup.openPopup(computer);
    }

    public JSearchableTextFlowController getComputerName() {
        return computerName;
    }

    public void setComputerName(JSearchableTextFlowController computerName) {
        this.computerName = computerName;
    }
}

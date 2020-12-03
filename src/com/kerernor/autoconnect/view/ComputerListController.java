package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Computer;
import com.kerernor.autoconnect.model.ComputerData;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ComputerListController extends ListView {

    @FXML
    private ListView<Computer> computerListView;

    private Pane paneBehind;

    private int current;

    public int getCurrent() {
        return current;
    }

    public ListView<Computer> getComputerListView() {
        return computerListView;
    }

    public void setPaneBehind(Pane paneBehind) {
        this.paneBehind = paneBehind;
    }

    public ComputerListController() {
        this(new Pane());
    }

    public ComputerListController(Pane paneBehind) {
        super();
        this.paneBehind = paneBehind;
        loadView();
    }

    @FXML
    public void initialize() {
        computerListView.getStyleClass().add("computer-list");
    }


    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.COMPUTER_LIST));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadList(FilteredList<Computer> computerFilteredList) {
        computerListView.setItems(computerFilteredList);

        computerListView.getItems().addListener((ListChangeListener<? super Computer>) observable -> {
            observable.next();
            int index = observable.getTo();
            computerListView.scrollTo(index - 1);
        });

        computerListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                current = ComputerData.getInstance().getComputerIndexInListByIP(newValue.getIp());
            }
        });

        computerListView.setCellFactory(computerListView1 -> {
            ComputerRowController currentComputer = new ComputerRowController(this.paneBehind);
            // fire this event
            //TODO: this event handler is redundant
            currentComputer.addEventFilter(KorEvents.ConnectVNCEvent.CONNECT_VNC_EVENT_EVENT, event -> {
                event.consume();
                fireEvent(event);
            });

            return currentComputer;
        });


    }


}

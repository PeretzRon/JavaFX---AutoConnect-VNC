package com.kerernor.autoconnect.view;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.KorEvents;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SearchAreaController extends TextField {

    @FXML
    private TextField search;

    public TextField getSearch() {
        return search;
    }

    public SearchAreaController() {
        loadView();
    }


    private void loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.SEARCH_AREA));
        loader.setController(this);
        loader.setRoot(this);

        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

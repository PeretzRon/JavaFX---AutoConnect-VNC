package com.kerernor.autoconnect.view.components;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;

public class SearchableTextFlowController {
    @FXML
    private Text text;

    @FXML
    public void initialize() {

    }

    public SearchableTextFlowController() {
        loadView();
    }

    public void updated(String textFromSearchInput) {
        Text text = new Text(textFromSearchInput);
        text.setUnderline(true);
        TextFlow textFlow = new TextFlow();
        String labelText = text.getText();
        String[] arr = labelText.split(textFromSearchInput);
        for (String s : arr) {
            textFlow.getChildren().add(new Text(s));
            textFlow.getChildren().add(new Text(textFromSearchInput));
        }
    }

    private TextFlow loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.SEARCHEABLE));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

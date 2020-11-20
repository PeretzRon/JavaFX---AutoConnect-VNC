package com.kerernor.autoconnect.view.components;


import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JSearchableTextFlowController extends TextFlow{

    @FXML
    private TextFlow textFlow;

    // default values, can be change by setter
    private Font font = Font.font(16);
    private Color colorFoundText = Color.YELLOW;
    private Color colorOfText = Color.WHITE;

    Text textToAdd = new Text("");
    private final static Set<JSearchableTextFlowController> activeSearchableTextFlowMap = new HashSet<>();

    @FXML
    public void initialize() {
    }

    public JSearchableTextFlowController() {
        loadView();
    }

    public String getTextFromTextFlow() {
        StringBuilder textString = new StringBuilder();
        for (Node child : textFlow.getChildren()) {
            textString.append(((Text) child).getText());
        }

        return textString.toString();
    }

    public void setOriginalText() {
        String input = getTextFromTextFlow();
        textFlow.getChildren().clear();
        createTextAndAddToTextFlow(input, false, false);
    }

    public void initText(String text) {
        activeSearchableTextFlowMap.add(this);
        textFlow.getChildren().clear();
        createTextAndAddToTextFlow(text, false, false);
    }

    private void createTextAndAddToTextFlow(String text, boolean isWithUnderline, boolean isWithColor) {
        textToAdd = new Text(text);
        textToAdd.setFont(font);
        textToAdd.setFill(colorOfText);
        if (isWithUnderline) {
            textToAdd.setUnderline(true);
        }

        if (isWithColor) {
            textToAdd.setFill(colorFoundText);
        }

        textFlow.getChildren().add(textToAdd);
    }

    /**
     * updated underline or color of the text
     *
     * @param textFromSearchInput - the text from the textField that filter that text
     */
    public void updatedText(String textFromSearchInput) {
        String currentText = getTextFromTextFlow();

        if (currentText.equalsIgnoreCase(textFromSearchInput)) {
            this.textFlow.getChildren().clear();
            createTextAndAddToTextFlow(textFromSearchInput, false, true);
            return;
        }

        updateTextInternal(currentText, textFromSearchInput);
    }

    private void updateTextInternal(String currentText, String textFromSearchInput) {
        String[] arr = currentText.split(textFromSearchInput);

        if (arr.length != 0) {
            this.textFlow.getChildren().clear();
        }

        for (int i = 0; i < arr.length; i++) {
            createTextAndAddToTextFlow(arr[i], false, false);
            if (i == arr.length - 1 && currentText.endsWith(textFromSearchInput)) {
                createTextAndAddToTextFlow(textFromSearchInput, false, true);
                continue;
            } else if (i == arr.length - 1) {
                continue;
            }

            createTextAndAddToTextFlow(textFromSearchInput, false, true);
        }
    }

    /**
     * this method load the fxml
     *
     * @return the controller
     */
    private TextFlow loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.J_SEARCHABLE_TEXT_FLOW));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Getters and Setters

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        setOriginalText();
    }

    public void setColorFoundText(Color colorFoundText) {
        this.colorFoundText = colorFoundText;
        setOriginalText();
    }

    public Color getColorFoundText() {
        return colorFoundText;
    }

    public static Set<JSearchableTextFlowController> getActiveSearchableTextFlowMap() {
        return activeSearchableTextFlowMap;
    }
}

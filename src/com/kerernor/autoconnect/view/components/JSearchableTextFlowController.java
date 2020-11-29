package com.kerernor.autoconnect.view.components;


import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class JSearchableTextFlowController extends TextFlow {

    @FXML
    private TextFlow textFlow;

    private String originalText;

    // default values, can be change by setter
    private Font font = Font.font(16);
    private Color colorFoundText = Color.YELLOW;
    private Color colorOfText = Color.WHITE;
    Text textToAdd = new Text("");
    private Logger logger = Logger.getLogger(JSearchableTextFlowController.class);

    List<Integer> listOfIndexThatFoundOnKMP = new ArrayList<>();


    @FXML
    public void initialize() {

    }

    public JSearchableTextFlowController() {
        loadView();
    }

    public void setOriginalText() {
        textFlow.getChildren().clear();
        createTextAndAddToTextFlow(originalText, false, false);
    }

    public void initText(String text) {
        originalText = text;
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
            textToAdd.setUnderline(true);
        }

        textFlow.getChildren().add(textToAdd);
    }

    /**
     * updated underline or color of the text
     *
     * @param textFromSearchInput - the text from the textField that filter that text
     */
    public void updatedText(String textFromSearchInput) {
        if (originalText.equalsIgnoreCase(textFromSearchInput)) {
            this.textFlow.getChildren().clear();
            createTextAndAddToTextFlow(originalText, false, true);
            return;
        }

        if (originalText.toLowerCase().contains(textFromSearchInput.toLowerCase())) {
            updateTextInternal(textFromSearchInput);
        }
    }

    private void updateTextInternal(String textFromSearchInput) {
        String originalTextLowerCase = originalText.toLowerCase();
        listOfIndexThatFoundOnKMP.clear();
        KMPSearch(textFromSearchInput.toLowerCase(), originalTextLowerCase);

        Collections.sort(listOfIndexThatFoundOnKMP, Comparator.comparingInt(o -> o));
        if (listOfIndexThatFoundOnKMP.size() > 0) {
            this.textFlow.getChildren().clear();
            int indexOfCurrentItemInMatchesList = 0;
            int i;
            char[] orginialTextInCharsArray = originalText.toCharArray();
            int indexToSubStringFromOriginalText = listOfIndexThatFoundOnKMP.get(0);
            for (i = 0; i < orginialTextInCharsArray.length; i++) {
                if (indexToSubStringFromOriginalText == i) {
                    createTextAndAddToTextFlow(originalText.substring(i, i + textFromSearchInput.length()), false, true);
                    indexOfCurrentItemInMatchesList++;
                    if (indexOfCurrentItemInMatchesList >= listOfIndexThatFoundOnKMP.size()) {
                        i = i + textFromSearchInput.length();
                        break;
                    } else {
                        i = i + textFromSearchInput.length() - 1;
                    }
                    do {
                        if (indexOfCurrentItemInMatchesList >= listOfIndexThatFoundOnKMP.size()) {
                            indexToSubStringFromOriginalText = orginialTextInCharsArray.length;
                            break;
                        }
                        indexToSubStringFromOriginalText = listOfIndexThatFoundOnKMP.get(indexOfCurrentItemInMatchesList);
                        if (indexToSubStringFromOriginalText <= i) {
                            indexOfCurrentItemInMatchesList++;
                        }
                    } while (indexToSubStringFromOriginalText <= i);
                } else {
                    createTextAndAddToTextFlow(originalText.substring(i, indexToSubStringFromOriginalText), false, false);
                    i = indexToSubStringFromOriginalText - 1;
                }
            }

            createTextAndAddToTextFlow(originalText.substring(i, orginialTextInCharsArray.length), false, false);
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

    public Color getColorOfText() {
        return colorOfText;
    }

    public void setColorOfText(Color colorOfText) {
        this.colorOfText = colorOfText;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setAlignment(TextAlignment Alignment) {
        textFlow.setTextAlignment(Alignment);
    }

    private List<String> permute(String input) {
        List<String> list = new ArrayList<>();
        int n = input.length();

        // Number of permutations is 2^n
        int max = 1 << n;

        // Converting string to lower case
        input = input.toLowerCase();

        // Using all subsequences and permuting them
        for (int i = 0; i < max; i++) {
            char combination[] = input.toCharArray();

            // If j-th bit is set, we convert it to upper case
            for (int j = 0; j < n; j++) {
                if (((i >> j) & 1) == 1)
                    combination[j] = (char) (combination[j] - 32);
            }

            // Printing current combination
            list.add(new String(combination));
        }

        return list;
    }

    private void KMPSearch(String pat, String txt) {
        int M = pat.length();
        int N = txt.length();

        // create lps[] that will hold the longest
        // prefix suffix values for pattern
        int lps[] = new int[M];
        int j = 0; // index for pat[]

        // Preprocess the pattern (calculate lps[]
        // array)
        computeLPSArray(pat, M, lps);

        int i = 0; // index for txt[]
        while (i < N) {
            if (pat.charAt(j) == txt.charAt(i)) {
                j++;
                i++;
            }
            if (j == M) {
                listOfIndexThatFoundOnKMP.add(i - j);
                j = lps[j - 1];
            }

            // mismatch after j matches
            else if (i < N && pat.charAt(j) != txt.charAt(i)) {
                // Do not match lps[0..lps[j-1]] characters,
                // they will match anyway
                if (j != 0)
                    j = lps[j - 1];
                else
                    i = i + 1;
            }
        }
    }

    private void computeLPSArray(String pat, int M, int lps[]) {
        // length of the previous longest prefix suffix
        int len = 0;
        int i = 1;
        lps[0] = 0; // lps[0] is always 0

        // the loop calculates lps[i] for i = 1 to M-1
        while (i < M) {
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else // (pat[i] != pat[len])
            {
                // This is tricky. Consider the example.
                // AAACAAAA and i = 7. The idea is similar
                // to search step.
                if (len != 0) {
                    len = lps[len - 1];

                    // Also, note that we do not increment
                    // i here
                } else // if (len == 0)
                {
                    lps[i] = len;
                    i++;
                }
            }
        }
    }

    @Override
    public String toString() {
        return originalText;
    }

}

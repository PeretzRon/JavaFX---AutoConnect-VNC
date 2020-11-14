package com.kerernor.autoconnect.view.components;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.io.IOException;

public class JTextFieldController extends StackPane {

    @FXML
    private StackPane mainPane;
    @FXML
    private TextField textField;
    @FXML
    private Label placeHolder;
    @FXML
    Rectangle lineUnderTextField;
    @FXML
    Line lineDraw;

    private TranslateTransition translateTransitionForPlaceHolder;
    private FillTransition fillTransitionForLine;
    private ParallelTransition parallelTransition;
    private final ObjectProperty<Color> colorLineUnderTextFieldNotFocus = new SimpleObjectProperty<>(Color.rgb(40, 20, 190));
    private final ObjectProperty<Color> colorLineUnderTextFieldFocus = new SimpleObjectProperty<>(Color.rgb(87, 134, 245));
    private final ObjectProperty<Font> textFieldFont = new SimpleObjectProperty<>(Font.font(15));
    private final StringProperty textFieldColor = new SimpleStringProperty("#000");
    private final ObjectProperty<Color> placeHolderColor = new SimpleObjectProperty<>(Color.BLUE);
    private final ObjectProperty<Font> placeHolderFont = new SimpleObjectProperty<>(Font.font(15));
    private final ObjectProperty<Font> placeHolderFontActive = new SimpleObjectProperty<>(Font.font(12));
    private final ObjectProperty<Color> lineEffectColor = new SimpleObjectProperty<>(Color.rgb(87, 134, 245));
    private Color colorPlaceHolderFocusNotActive = Color.GRAY;
    private Color colorPlaceHolderFocusActive = Color.BLUE;
    private double fontPlaceHolderNotActive = 15;
    private double fontPlaceHolderActive = 12;
    private final double TIME_FOR_TRANSLATE_LABEL = 150;
    private final double TIME_FOR_LINE_COLOR_CHANGE = 150;
    private final double TIME_FOR_EFFECT_LINE_ON_UNDER_LINE_WHEN_FOCUS = 100;
    private boolean isPlaceHolderUp = false;
    final Timeline timelineOnFocus = new Timeline();
    final Timeline timelineOnEndFocus = new Timeline();

    public JTextFieldController() {
        loadView();
    }

    /**
     * init nodes and listeners
     */
    @FXML
    private void initialize() {
        initControllers();
        addTextFieldListeners();
        addTimeLineListeners();
        Platform.runLater(this::afterInitialize);
    }

    private void initControllers() {
        lineDraw.setVisible(false);
        translateTransitionForPlaceHolder = new TranslateTransition(Duration.millis(TIME_FOR_TRANSLATE_LABEL), placeHolder);
        lineUnderTextField.fillProperty().bind(colorLineUnderTextFieldNotFocus);

        placeHolder.textFillProperty().bind(placeHolderColor);
        placeHolder.fontProperty().bind(placeHolderFont);

        textField.fontProperty().bind(textFieldFont);
        textField.styleProperty().bind(Bindings.concat("-fx-border-color:  transparent transparent transparent transparent;", "-fx-background-color:  transparent;", "-fx-text-fill: ", textFieldColor, ";"));

        lineDraw.strokeProperty().bind(lineEffectColor);
        initAnimations();
    }

    private void initAnimations() {
        fillTransitionForLine = new FillTransition(Duration.millis(TIME_FOR_LINE_COLOR_CHANGE), lineUnderTextField);
        parallelTransition = new ParallelTransition(translateTransitionForPlaceHolder, fillTransitionForLine);
    }

    private void addTimeLineListeners() {
        timelineOnEndFocus.setOnFinished(event -> {
            lineDraw.setVisible(false);
        });
    }

    private void addTextFieldListeners() {
        textField.focusedProperty().addListener((observable, oldValue, isFocusActive) -> {
            boolean isTextFieldEmpty = textField.getText().trim().isEmpty();
            if (isFocusActive && isTextFieldEmpty) {
                onFocusActive(true);
            } else if (isTextFieldEmpty) {
                // focus not active and there is not text
                onFocusNoActive();
            }
        });

        textField.textProperty().isEmpty().addListener((observable, oldValue, isEmpty) -> {
            if (!isEmpty && !isPlaceHolderUp) {
                onFocusActive(false);
            }
        });
    }

    private void afterInitialize() {
        double width = mainPane.getPrefWidth();
        lineUnderTextField.setWidth(width);
        final KeyValue kvStart = new KeyValue(lineDraw.endXProperty(), lineUnderTextField.getWidth());
        final KeyFrame kfStart = new KeyFrame(Duration.millis(TIME_FOR_EFFECT_LINE_ON_UNDER_LINE_WHEN_FOCUS), kvStart);
        timelineOnFocus.getKeyFrames().add(kfStart);

        final KeyValue kvStop = new KeyValue(lineDraw.endXProperty(), 0);
        final KeyFrame kfStop = new KeyFrame(Duration.millis(TIME_FOR_EFFECT_LINE_ON_UNDER_LINE_WHEN_FOCUS), kvStop);
        timelineOnEndFocus.getKeyFrames().add(kfStop);
    }

    private void onFocusActive(boolean isWithAnimation) {
        isPlaceHolderUp = true;
        parallelTransition.stop();
        placeHolderColor.setValue(this.colorPlaceHolderFocusActive);
        placeHolderFontActive.setValue(Font.font(fontPlaceHolderActive));
        placeHolderFont.setValue(Font.font(this.fontPlaceHolderActive));
        translateTransitionForPlaceHolder.setAutoReverse(true);
        translateTransitionForPlaceHolder.setByY(-23);
        fillTransitionForLine.toValueProperty().bind(colorLineUnderTextFieldFocus);

        if (!isWithAnimation) {
            translateTransitionForPlaceHolder.setDuration(new Duration(0));
        } else {
            translateTransitionForPlaceHolder.setDuration(new Duration(TIME_FOR_TRANSLATE_LABEL));
        }

        lineDraw.setVisible(true);
        timelineOnFocus.play();

        lineUnderTextField.fillProperty().unbind();
        parallelTransition.play();
    }

    private void onFocusNoActive() {
        isPlaceHolderUp = false;
        parallelTransition.stop();
        textField.clear();
        placeHolderColor.setValue(this.colorPlaceHolderFocusNotActive);
        placeHolderFont.setValue(Font.font(this.fontPlaceHolderNotActive));
        translateTransitionForPlaceHolder.setAutoReverse(true);
        translateTransitionForPlaceHolder.setByY(23);
        fillTransitionForLine.toValueProperty().bind(colorLineUnderTextFieldNotFocus);


        timelineOnEndFocus.play();
//        lineDraw.setStartX(0);
//        lineDraw.setEndX(0);

        parallelTransition.play();
    }

    public void setInitData(String placeHolderText, double fontSize, Color colorPlaceHolderFocusActive, Color colorLineFocusNotActive, String textFieldColor) {
        placeHolder.setText(placeHolderText);
        textFieldFont.setValue(new Font(fontSize));
        setColorPlaceHolderFocusActive(colorPlaceHolderFocusActive);
        setColorPlaceHolderFocusNotActive(colorLineFocusNotActive);
        setTextFieldColor(textFieldColor);
    }

    public void setInitData(String placeHolderText, double fontSize) {
        placeHolder.setText(placeHolderText);
        textFieldFont.setValue(new Font(fontSize));
    }

    public StackPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.J_TEXT_FIELD));

        loader.setController(this);
        loader.setRoot(this);
        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public TextField getTextField() {
        return textField;
    }

    public void setTextField(TextField textField) {
        this.textField = textField;
    }

    public Label getPlaceHolder() {
        return placeHolder;
    }

    public void setPlaceHolder(Label placeHolder) {
        this.placeHolder = placeHolder;
    }

    public Color getColorLineFocusActive() {
        return colorLineUnderTextFieldFocus.get();
    }

    public void setColorLineFocusActive(Color colorLineFocusActive) {
        colorLineUnderTextFieldFocus.setValue(colorLineFocusActive);
    }

    public Color getColorLineFocusNotActive() {
        return colorLineUnderTextFieldNotFocus.get();
    }

    public void setColorLineFocusNotActive(Color colorLineFocusNotActive) {
        colorLineUnderTextFieldNotFocus.setValue(colorLineFocusNotActive);
    }

    public Color getColorPlaceHolderFocusNotActive() {
        return colorPlaceHolderFocusNotActive;
    }

    public void setColorPlaceHolderFocusNotActive(Color colorPlaceHolderFocusNotActive) {
        placeHolderColor.setValue(colorPlaceHolderFocusNotActive);
        this.colorPlaceHolderFocusNotActive = colorPlaceHolderFocusNotActive;
    }

    public Color getColorPlaceHolderFocusActive() {
        return colorPlaceHolderFocusActive;
    }

    public void setColorPlaceHolderFocusActive(Color colorPlaceHolderFocusActive) {
        this.colorPlaceHolderFocusActive = colorPlaceHolderFocusActive;
    }

    public Font getTextFieldFont() {
        return textFieldFont.get();
    }

    public double textFieldFontProperty() {
        return textFieldFont.get().getSize();
    }

    public void setTextFieldFont(double textFieldFont) {
        this.textFieldFont.set(new Font(textFieldFont));
    }

    public String getTextFieldColor() {
        return textFieldColor.get();
    }

    public String textFieldColorProperty() {
        return textFieldColor.get();
    }

    public void setTextFieldColor(String textFieldColor) {
        this.textFieldColor.set(textFieldColor);
    }

    public double getFontPlaceHolderNotActive() {
        return fontPlaceHolderNotActive;
    }

    public void setFontPlaceHolderNotActive(double fontPlaceHolderNotActive) {
        placeHolderFont.setValue(Font.font(fontPlaceHolderNotActive));
        this.fontPlaceHolderNotActive = fontPlaceHolderNotActive;
    }

    public double getFontPlaceHolderActive() {
        return fontPlaceHolderActive;
    }

    public void setFontPlaceHolderActive(double fontPlaceHolderActive) {
        this.fontPlaceHolderActive = fontPlaceHolderActive;
    }

    public Color getLineEffectColor() {
        return lineEffectColor.get();
    }

    public Color lineEffectColorProperty() {
        return lineEffectColor.get();
    }

    public void setLineEffectColor(Color lineEffectColor) {
        this.lineEffectColor.set(lineEffectColor);
    }

}

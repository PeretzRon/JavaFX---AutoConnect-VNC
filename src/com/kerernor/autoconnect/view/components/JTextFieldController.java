package com.kerernor.autoconnect.view.components;

import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.Utils;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
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
import org.apache.log4j.Logger;

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

    private Logger logger = Logger.getLogger(JTextFieldController.class);
    private TranslateTransition translateTransitionForPlaceHolder;
    private ParallelTransition parallelTransition;
    private final ObjectProperty<Color> colorLineUnderTextField = new SimpleObjectProperty<>(Color.rgb(40, 20, 190));
    private final ObjectProperty<Font> textFieldFont = new SimpleObjectProperty<>(Font.font(15));
    private final StringProperty textFieldColor = new SimpleStringProperty("#000");
    private final DoubleProperty underLineWidth = new SimpleDoubleProperty(0);
    private final ObjectProperty<Color> placeHolderColor = new SimpleObjectProperty<>(Color.GRAY);
    private final ObjectProperty<Font> placeHolderFont = new SimpleObjectProperty<>(Font.font(15));
    private final ObjectProperty<Font> placeHolderFontActive = new SimpleObjectProperty<>(Font.font(12));
    private final ObjectProperty<Color> lineEffectColor = new SimpleObjectProperty<>(Color.rgb(87, 134, 245));
    private Color colorPlaceHolderFocusNotActive = Color.GRAY;
    private Color colorPlaceHolderFocusActive = Color.rgb(87, 134, 245);
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
        logger.trace("initialize");
        textField.setFocusTraversable(false);
        lineUnderTextField.widthProperty().bind(underLineWidth);
        initControllers();
        addTextFieldListeners();
        addTimeLineListeners();
        lineDraw.setVisible(false);
        Platform.runLater(this::afterInitialize);
    }

    private void initControllers() {
        translateTransitionForPlaceHolder = new TranslateTransition(Duration.millis(TIME_FOR_TRANSLATE_LABEL), placeHolder);
        lineUnderTextField.fillProperty().bind(colorLineUnderTextField);

        placeHolder.textFillProperty().bind(placeHolderColor);
        placeHolder.fontProperty().bind(placeHolderFont);

        textField.fontProperty().bind(textFieldFont);
        textField.styleProperty().bind(Bindings.concat("-fx-border-color:  transparent transparent transparent transparent;", "-fx-background-color:  transparent;", "-fx-text-fill: ", textFieldColor, ";"));

        lineDraw.strokeProperty().bind(lineEffectColor);
        lineUnderTextField.fillProperty().bind(colorLineUnderTextField);
        initAnimations();
    }

    private void initAnimations() {
        parallelTransition = new ParallelTransition(translateTransitionForPlaceHolder);
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
                // focus active and there is no text
                onFocusActive(true);
            } else if (!isFocusActive && isTextFieldEmpty) {
                // focus not active and there is no text
                onFocusNoActive();
            } else if (isFocusActive) {
                // focus active
                lineDraw.setVisible(true);
                timelineOnFocus.play();
            } else {
                timelineOnEndFocus.play();
            }
        });

        textField.textProperty().addListener((observable, oldValue, isEmpty) -> {
            if (!textField.getText().isEmpty() && !isPlaceHolderUp) {
                onFocusActive(false);
            }
        });
    }

    private void afterInitialize() {
        logger.trace("afterInitialize");
        final KeyValue kvStart = new KeyValue(lineDraw.endXProperty(), lineUnderTextField.getWidth());
        final KeyFrame kfStart = new KeyFrame(Duration.millis(TIME_FOR_EFFECT_LINE_ON_UNDER_LINE_WHEN_FOCUS), kvStart);
        timelineOnFocus.getKeyFrames().add(kfStart);

        final KeyValue kvStop = new KeyValue(lineDraw.endXProperty(), 0);
        final KeyFrame kfStop = new KeyFrame(Duration.millis(TIME_FOR_EFFECT_LINE_ON_UNDER_LINE_WHEN_FOCUS), kvStop);
        timelineOnEndFocus.getKeyFrames().add(kfStop);
        lineDraw.setVisible(false);
    }

    private void onFocusActive(boolean isWithAnimation) {
        logger.trace("onFocusActive - Animation: " + isWithAnimation);
        isPlaceHolderUp = true;
        parallelTransition.stop();
        timelineOnFocus.stop();
        placeHolderColor.setValue(this.colorPlaceHolderFocusActive);
        placeHolderFontActive.setValue(Font.font(fontPlaceHolderActive));
        placeHolderFont.setValue(Font.font(this.fontPlaceHolderActive));
        translateTransitionForPlaceHolder.setAutoReverse(true);
        translateTransitionForPlaceHolder.setByY(-23);

        if (isWithAnimation) {
            translateTransitionForPlaceHolder.setDuration(Duration.millis(TIME_FOR_TRANSLATE_LABEL));
            lineDraw.setVisible(true);
            timelineOnFocus.play();
        } else {
            translateTransitionForPlaceHolder.setDuration(Duration.millis(TIME_FOR_TRANSLATE_LABEL / 2));
        }

        parallelTransition.play();
    }

    private void onFocusNoActive() {
        logger.trace("onFocusNoActive");
        isPlaceHolderUp = false;
        parallelTransition.stop();
        timelineOnEndFocus.stop();
        textField.clear();
        placeHolderColor.setValue(this.colorPlaceHolderFocusNotActive);
        placeHolderFont.setValue(Font.font(this.fontPlaceHolderNotActive));
        translateTransitionForPlaceHolder.setAutoReverse(true);
        translateTransitionForPlaceHolder.setByY(23);

        timelineOnEndFocus.play();
        parallelTransition.play();
    }

    public void setInitData(String placeHolderText, double fontSize, Color colorPlaceHolderFocusActive, Color colorLineFocusNotActive, String textFieldColor, double underLineWidth) {
        placeHolder.setText(placeHolderText);
        textFieldFont.setValue(new Font(fontSize));
        setColorPlaceHolderFocusActive(colorPlaceHolderFocusActive);
        setColorPlaceHolderFocusNotActive(colorLineFocusNotActive);
        setTextFieldColor(textFieldColor);
        setUnderLineWidth(underLineWidth);
    }

    public void setInitData(String placeHolderText, double fontSize, double underLineWidth) {
        placeHolder.setText(placeHolderText);
        textFieldFont.setValue(new Font(fontSize));
        setUnderLineWidth(underLineWidth);
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

    public Color getColorUnderLineFocusNotActive() {
        return colorLineUnderTextField.get();
    }

    public void setColorUnderLineFocusNotActive(Color colorLineFocusNotActive) {
        colorLineUnderTextField.setValue(colorLineFocusNotActive);
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

    public double getUnderLineWidth() {
        return underLineWidth.get();
    }

    public void setUnderLineWidth(double underLineWidth) {
        this.underLineWidth.set(underLineWidth);
    }
}

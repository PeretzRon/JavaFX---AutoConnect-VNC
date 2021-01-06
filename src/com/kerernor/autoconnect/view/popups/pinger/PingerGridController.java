package com.kerernor.autoconnect.view.popups.pinger;

import animatefx.animation.*;
import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PingerGridController extends BorderPane {


    @FXML
    private FlowPane mainPane;
    @FXML
    private HBox buttonsHBox;
    @FXML
    private Button saveChangesButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button test;
    @FXML
    private StackPane dusbinStackPane;
    @FXML
    private ImageView dusbinImageView;


    private Parent paneBehind;
    private Stage stage;
    private ObservableList<Pinger> items;
    private final Logger logger = LogManager.getLogger(PingerGridController.class);

    private double x, y;
    public static final int DELTA_TRANSLATE_Y = 100;
    private PingerController draggedItem;
    private PingerController colorItem;
    private final int gridRows = 6;
    private final double spaceBetweenCells = 10d;
    private boolean isSingleDragging = true;
    private List<PingerController> selectionList = new ArrayList<>();
    private BooleanProperty isShowIDustbinImage = new SimpleBooleanProperty(false);
    private boolean isTrashEnlarged = false;

    public PingerGridController(ObservableList<Pinger> items, Parent paneBehind) {
        logger.debug("LIST SIZE: " + items.size());
        this.items = items;
        this.paneBehind = paneBehind;
    }

    @FXML
    public void initialize() {
        mainPane.setVgap(spaceBetweenCells);
        mainPane.setHgap(spaceBetweenCells);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        dusbinStackPane.setVisible(false);
        mainPane.setOrientation(Orientation.HORIZONTAL);
        createCells();
        createEmptyCells();
        mainPane.setOnMousePressed(this::onMousePress);
        mainPane.setOnMouseDragged(this::onMouseDragged);
        mainPane.setOnMouseReleased(this::onMouseReleased);
        saveChangesButton.setOnMouseClicked(this::saveChangesHandler);
        closeButton.setOnMouseClicked(this::closeClickAction);
        test.setOnMouseClicked(event -> {
            logger.debug("");
//            isSingleDragging = !isSingleDragging;
        });
    }

    private void closeClickAction(MouseEvent event) {
        closeClickAction();
    }

    private void createCells() {
        for (int i = 0; i < items.size(); i++) {
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setFirstRow(items.get(i).getName());
            pingerController.setState(KorTypes.PingerGridItemState.ACTIVE);
            pingerController.setPinger(items.get(i));
            pingerController.getStyleClass().setAll("main-pane-not-empty");
            mainPane.getChildren().add(pingerController);
        }
    }

    private void createEmptyCells() {
        for (int i = items.size(); i < Utils.MAX_PINGER_GROUPS * 2; i++) {
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setState(KorTypes.PingerGridItemState.EMPTY);
            pingerController.getStyleClass().add("main-pane");
            mainPane.getChildren().add(pingerController);
        }

        for (int i = Utils.MAX_PINGER_GROUPS; i < Utils.MAX_PINGER_GROUPS * 2; i++) {
            mainPane.getChildren().get(i).setTranslateY(DELTA_TRANSLATE_Y);
        }
    }

    private void saveChangesHandler(MouseEvent event) {
        ObservableList<Pinger> list = FXCollections.observableArrayList();

        for (Node node : mainPane.getChildren()) {
            if (((PingerController) node).getState() != KorTypes.PingerGridItemState.EMPTY) {
                list.add(((PingerController) node).getPinger());
            }
        }

        System.out.println(list);
        PingerData.getInstance().getPingerObservableList().setAll(list);
        closeClickAction();
    }


    private void onMousePress(MouseEvent event) {
        final PingerController item = findItemByCoordinates(event.getSceneX(), event.getSceneY(), false);

        if (isSingleDragging) {

            double moduleHeight = ((PingerController) mainPane.getChildren().get(0)).getPrefHeight();
            double delta = -(moduleHeight * gridRows + spaceBetweenCells * gridRows);
            if (item != null && item.getState() != KorTypes.PingerGridItemState.EMPTY) {
                int indexOnGrid = item.getIndexOnGrid();
                dusbinStackPane.setVisible(true);
                Platform.runLater(() -> {
                    ZoomIn tada = new ZoomIn(dusbinStackPane);
                    tada.setSpeed(2);
                    tada.play();
                });
                ((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS)).setFirstRow(item.getFirstRow());
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("main-pane-not-empty");
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("menu-item-dragged-border");
                scaleNode((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS), 1.1);
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).setTranslateY(delta);
                y = event.getSceneY();
                x = event.getSceneX();


            }
        } else {
            selectController(item);
        }

    }

    private void selectController(PingerController item) {
        if (item != null) {
            item.getStyleClass().add("menu-item-hover");
            selectionList.add(item);
        }
    }

    private void onMouseDragged(MouseEvent event) {
        if (isSingleDragging) {
            final ObservableList<Node> pingerControllers = mainPane.getChildren();
            double moduleHeight = ((PingerController) pingerControllers.get(Utils.MAX_PINGER_GROUPS)).getPrefHeight();
            double delta = -(moduleHeight * gridRows + spaceBetweenCells * gridRows);
            if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
                restoreControllerVisibilityAndPosition(event, pingerControllers, delta);
                makeBorderOnHoverController(event, pingerControllers);
            }

            isOnTrash(event.getSceneX(), event.getSceneY());


        } else {
            final ObservableList<Node> pingerControllers = mainPane.getChildren();
            double moduleHeight = ((PingerController) pingerControllers.get(Utils.MAX_PINGER_GROUPS)).getPrefHeight();
            double delta = -(moduleHeight * gridRows + spaceBetweenCells * gridRows);
            for (PingerController pingerController : selectionList) {
                if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
                    restoreControllerVisibilityAndPosition(event, pingerControllers, delta);
                    makeBorderOnHoverController(event, pingerControllers);
                }
            }
        }

    }

    private void makeBorderOnHoverController(MouseEvent event, ObservableList<Node> pingerControllers) {
        PingerController itemControllerHover = findItemByCoordinates(event.getSceneX(), event.getSceneY(), true);
        if (itemControllerHover != null && !itemControllerHover.equals(draggedItem)) {
            if (!itemControllerHover.getStyleClass().contains("menu-item-hover")) {
                itemControllerHover.getStyleClass().add("menu-item-hover");
                colorItem = itemControllerHover;
            }
        } else {
            if (colorItem != null) {
                removeBorderFromAllNodes(pingerControllers);
            }
        }
    }

    private void removeBorderFromAllNodes(ObservableList<Node> pingerControllers) {
        for (Node child : pingerControllers) {
            child.getStyleClass().remove("menu-item-hover");
        }
    }

    private void restoreControllerVisibilityAndPosition(MouseEvent event, ObservableList<Node> pingerControllers, double delta) {
        pingerControllers.get(draggedItem.getIndexOnGrid()).getStyleClass().remove("main-pane-not-empty");
        pingerControllers.get(draggedItem.getIndexOnGrid()).setVisible(false);

        pingerControllers.get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateY(delta + event.getSceneY() - y);
        pingerControllers.get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateX(event.getSceneX() - x);
    }


    private void onMouseReleased(MouseEvent event) {
        if (isSingleDragging) {
            if (isTrashEnlarged) {
                if (draggedItem != null) {
                    deleteItem(draggedItem);
                }
                isTrashEnlarged = false;
            }
            if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
                mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateY(DELTA_TRANSLATE_Y);
                mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateX(0);
                if (draggedItem.isDeleted()) {
                    mainPane.getChildren().get(draggedItem.getIndexOnGrid()).getStyleClass().add("main-pane");
                } else {
                    mainPane.getChildren().get(draggedItem.getIndexOnGrid()).getStyleClass().add("main-pane-not-empty");
                }

                mainPane.getChildren().get(draggedItem.getIndexOnGrid()).setVisible(true);
                scaleNode((PingerController) mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS), 1);
            }

            if (colorItem != null && draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY && !draggedItem.isDeleted()) {
                colorItem.getStyleClass().remove("menu-item-hover");
                switchModules();
            }
            removeBorderFromAllNodes(mainPane.getChildren());
            draggedItem = null;
            colorItem = null;

            dusbinImageView.setFitHeight(30);
            dusbinImageView.setFitWidth(30);


            ZoomOut zoomOut = new ZoomOut(dusbinStackPane);
            zoomOut.setSpeed(3);
            zoomOut.setOnFinished(event1 -> {
                dusbinStackPane.setVisible(false);
            });

            zoomOut.setResetOnFinished(true).play();
        }
    }

    private void deleteItem(PingerController draggedItem) {
        logger.debug("Deleting: " + draggedItem.getPinger().getName());
        draggedItem.setDeleted(true);
        PingerController emptyPingerController = PingerController.emptyPingerController();
        emptyPingerController.setIndexOnGrid(draggedItem.getIndexOnGrid());
        mainPane.getChildren().set(draggedItem.getIndexOnGrid(), emptyPingerController);
        createEffectOfSwappedControllers(emptyPingerController);
    }

    private void switchModules() {
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(mainPane.getChildren());
        Collections.swap(workingCollection, colorItem.getIndexOnGrid(), draggedItem.getIndexOnGrid());
        mainPane.getChildren().setAll(workingCollection);
        swapIndexOngGrid(colorItem, draggedItem);
        createEffectOfSwappedControllers(colorItem);
        createEffectOfSwappedControllers(draggedItem);
    }

    private void createEffectOfSwappedControllers(PingerController item) {
        ZoomIn zoomIn = new ZoomIn(item);
        zoomIn.setSpeed(2);
        zoomIn.play();
    }

    private void swapIndexOngGrid(PingerController ctl1, PingerController ctl2) {
        int temp = ctl1.getIndexOnGrid();
        ctl1.setIndexOnGrid(ctl2.getIndexOnGrid());
        ctl2.setIndexOnGrid(temp);
    }

    private boolean isOnTrash(double x, double y) {
        double startX, startY, endX, endY;
        startX = dusbinStackPane.getLayoutX();
        endX = startX + dusbinStackPane.getWidth();
        startY = dusbinStackPane.getLayoutY();
        endY = startY + dusbinStackPane.getHeight();

        if ((startX < x && endX > x) && (startY < y && endY > y)) {
            dusbinImageView.setFitHeight(60);
            dusbinImageView.setFitWidth(60);
            dusbinStackPane.toFront();
            isTrashEnlarged = true;
        } else {
            dusbinImageView.setFitHeight(30);
            dusbinImageView.setFitWidth(30);
            isTrashEnlarged = false;
        }
        return false;
    }


    private PingerController findItemByCoordinates(double x, double y, boolean isForStyling) {
        double startX, startY, endX, endY;
        PingerController itemController;
        for (Node currentItemController : mainPane.getChildren()) {
            itemController = (PingerController) currentItemController;
            startX = currentItemController.getLayoutX();
            endX = startX + ((PingerController) currentItemController).getWidth();
            startY = currentItemController.getLayoutY() + 30;
            endY = startY + ((PingerController) currentItemController).getHeight();

            if ((startX < x && endX > x) && (startY < y && endY > y)) {
                if (!isForStyling) {
                    draggedItem = itemController;
                }
                return itemController;
            }
        }

        return null;
    }

    private void scaleNode(PingerController itemController, double value) {
        itemController.setScaleX(value);
        itemController.setScaleY(value);
    }


    public BorderPane loadView() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(Utils.PINGER_POPUP_GRID));
        loader.setController(this);
        loader.setRoot(this);

        try {
            return loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void show() {
        Scene scene = new Scene(this.loadView(), 770, 556);
        stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        if (Utils.CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING) {
            // Set out of focus closing ability
            stage.focusedProperty().addListener((observableValue, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    closeClickAction();
                }
            });
        }

        // Prevent the window from closing in case of out of focus
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(paneBehind.getScene().getWindow());

        Utils.enableExitPopupOnEscKey(stage, callback -> closeClickAction());
        stage.show();

        // center stage
        Utils.centerNewStageToBehindStage(KorCommon.getInstance().getPingerScreenController(), stage);

        // Blur the pane behind
        paneBehind.effectProperty().setValue(Utils.getBlurEffect());
    }

    public void closeClickAction() {
        // Revert the blur effect from the pane behind
        paneBehind.effectProperty().setValue(Utils.getEmptyEffect());
        stage.close();
    }

}

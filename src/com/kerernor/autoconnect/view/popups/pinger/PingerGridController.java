package com.kerernor.autoconnect.view.popups.pinger;

import animatefx.animation.*;
import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.model.Pinger;
import com.kerernor.autoconnect.model.PingerData;
import com.kerernor.autoconnect.util.*;
import com.kerernor.autoconnect.view.popups.AlertPopupController;
import javafx.application.Platform;
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
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    private Button deleteCell;
    @FXML
    private Button castleCells;
    @FXML
    private StackPane dusbinStackPane;
    @FXML
    private ImageView dusbinImageView;
    @FXML
    private HBox dustbinHBox;
    @FXML
    private VBox bottomMenu;


    private final Parent paneBehind;
    private Stage stage;
    private final ObservableList<Pinger> items;
    private final Logger logger = LogManager.getLogger(PingerGridController.class);

    private double x, y;
    public static final int DELTA_TRANSLATE_Y = 100;
    private PingerController draggedItem;
    private PingerController colorItem;
    private final int GRID_ROW = 7;
    private final int GRID_COL = 8;
    private final double spaceBetweenCells = 10d;
    private final boolean isSingleDragging = true;
    private final List<PingerController> selectionList = new ArrayList<>();
    private boolean isTrashEnlarged = false;
    private boolean isDeleteCellSelected = false;
    private boolean isCastlingSelected = false;
    Tada tada;
    private ScheduledFuture<?> timer;

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
        dustbinHBox.setVisible(false);
        mainPane.setOrientation(Orientation.HORIZONTAL);
        createCells();
        createEmptyCells();
        initCellAnimation();
        mainPane.setOnMousePressed(this::onMousePress);
        mainPane.setOnMouseDragged(this::onMouseDragged);
        mainPane.setOnMouseReleased(this::onMouseReleased);
        saveChangesButton.setOnMouseClicked(this::saveChangesButtonHandler);
        closeButton.setOnMouseClicked(this::closeClickAction);
        deleteCell.setOnMouseClicked(this::onDeleteCellAction);
        castleCells.setOnMouseClicked(this::onCastleCellsAction);

        tada = new Tada(dusbinImageView);

    }

    private void onCastleCellsAction(MouseEvent event) {
        isCastlingSelected = !isCastlingSelected;
        if (isCastlingSelected) {
            castleCells.getStyleClass().add("border-yellow");
        } else {
            castleCells.getStyleClass().remove("border-yellow");
        }
    }

    private void onDeleteCellAction(MouseEvent event) {
        isDeleteCellSelected = !isDeleteCellSelected;
        if (isDeleteCellSelected) {
            deleteCell.getStyleClass().add("border-yellow");
        } else {
            deleteCell.getStyleClass().remove("border-yellow");
        }
    }


    private void closeClickAction(MouseEvent event) {
        closeClickAction();
    }

    private void createCells() {
        logger.debug("createCells");
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
        logger.debug("createEmptyCells");
        for (int i = items.size(); i < Utils.MAX_PINGER_GROUPS * 2; i++) {
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setState(KorTypes.PingerGridItemState.EMPTY);
            pingerController.getStyleClass().add("main-pane");
            mainPane.getChildren().add(pingerController);
        }

        createDummyController();
    }

    private void createDummyController() {
        logger.debug("createDummyController");
        for (int i = Utils.MAX_PINGER_GROUPS; i < Utils.MAX_PINGER_GROUPS * 2; i++) {
            final Node node = mainPane.getChildren().get(i);
            node.setTranslateY(DELTA_TRANSLATE_Y);
            ((PingerController) node).setState(KorTypes.PingerGridItemState.DUMMY);
        }
    }

    private void initCellAnimation() {
        Platform.runLater(() -> {
            boolean secondHalf = false;
            int half = GRID_COL / 2;
            int j = 0;
            for (int i = 0; i < Utils.MAX_PINGER_GROUPS; i++) {
                if (secondHalf) {
                    new FadeInRight(mainPane.getChildren().get(i)).play();
                } else {
                    new FadeInLeft(mainPane.getChildren().get(i)).play();
                }
                j++;
                if (j >= half) {
                    secondHalf = !secondHalf;
                    j = 0;
                }
            }
        });
    }

    private void saveChangesButtonHandler(MouseEvent event) {
        ObservableList<Pinger> list = FXCollections.observableArrayList();

        for (Node node : mainPane.getChildren()) {
            if (((PingerController) node).getState() == KorTypes.PingerGridItemState.ACTIVE) {
                list.add(((PingerController) node).getPinger());
            }
        }

        PingerData.getInstance().getPingerObservableList().setAll(list);
        closeClickAction();
    }

    private void toggleBottomMenu(boolean isToShowButtons) {
        buttonsHBox.setVisible(isToShowButtons);
        dustbinHBox.setVisible(!isToShowButtons);
    }

    private void longPressTimer(MouseEvent event) {
        cancelLongPressTimer();
        timer = ThreadManger.getInstance().getScheduledThreadPool().schedule(() -> {
            logger.debug("long press detected");
            handleWithMultiDragProcessing(event);
        }, 400, TimeUnit.MILLISECONDS);
    }

    private void cancelLongPressTimer() {
        if (timer != null) {
            timer.cancel(true);
            timer = null;
        }
    }

    private void onMousePress(MouseEvent event) {
        logger.debug("onMousePress");
        final PingerController item = findItemByCoordinates(event.getSceneX(), event.getSceneY(), false);
        if (isDeleteCellSelected) {
            deleteCellProcessing(item);
            return;
        }
        if (isCastlingSelected) {
            castlingCellsProcessing(item);
            return;
        }
        if (isSingleDragging) {
            double moduleHeight = ((PingerController) mainPane.getChildren().get(0)).getPrefHeight();
            double delta = -(moduleHeight * GRID_ROW + spaceBetweenCells * GRID_ROW);
            if (item != null && item.getState() != KorTypes.PingerGridItemState.EMPTY) {
                int indexOnGrid = item.getIndexOnGrid();
                toggleBottomMenu(false);
                Platform.runLater(() -> {
                    ZoomIn zoomIn = new ZoomIn(dustbinHBox);
                    zoomIn.setSpeed(3);
                    zoomIn.play();
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
            longPressTimer(event);
        }
    }

    private void castlingCellsProcessing(PingerController item) {
        if (item == null) return;
        selectController(item);
        if (selectionList.size() == 2) {
            switchModules(selectionList.get(0), selectionList.get(1));
            unSelectAllCells();
            onCastleCellsAction(null);
        }
    }

    private void deleteCellProcessing(PingerController item) {
        if (item == null || item.getState() != KorTypes.PingerGridItemState.ACTIVE) return;
        int indexOnGrid = item.getIndexOnGrid();
        PingerController emptyCell = PingerController.emptyPingerController();
        emptyCell.setIndexOnGrid(indexOnGrid);
        getGridCollection().set(indexOnGrid, emptyCell);
        getGridCollection().get(indexOnGrid).getStyleClass().add("main-pane");
        deleteCell.getStyleClass().remove("border-yellow");
        isDeleteCellSelected = false;
        ZoomIn zoomIn = new ZoomIn(emptyCell);
        zoomIn.setSpeed(3);
        zoomIn.play();
    }

    private void handleWithMultiDragProcessing(MouseEvent event) {
        logger.debug("handleWithMultiDragProcessing");
        double moduleHeight = ((PingerController) mainPane.getChildren().get(0)).getPrefHeight();
        double delta = -(moduleHeight * GRID_ROW + spaceBetweenCells * GRID_ROW);
        for (PingerController pingerController : selectionList) {
            if (pingerController != null && pingerController.getState() != KorTypes.PingerGridItemState.EMPTY) {
                int indexOnGrid = pingerController.getIndexOnGrid();
//                toggleBottomMenu(false);
//                Platform.runLater(() -> {
//                    ZoomIn zoomIn = new ZoomIn(dustbinHBox);
//                    zoomIn.setSpeed(3);
//                    zoomIn.play();
//                });
                ((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS)).setFirstRow(pingerController.getFirstRow());
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("main-pane-not-empty");
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("menu-item-dragged-border");
                scaleNode((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS), 1.1);
                mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).setTranslateY(delta);
            }
            y = event.getSceneY();
            x = event.getSceneX();
        }
    }

    private void selectController(PingerController item) {
        if (item != null) {
            for (PingerController pingerController : selectionList) {
                if (pingerController.getIndexOnGrid() == item.getIndexOnGrid()) {
                    return;
                }
            }
            item.getStyleClass().add("menu-item-hover");
            selectionList.add(item);
        }
    }

    private void unselectController(PingerController item) {
        if (item != null) {
            item.getStyleClass().remove("menu-item-hover");
            selectionList.remove(item);
        }
    }

    private void unSelectAllCells(){
        getGridCollection().forEach(cell -> unselectController((PingerController) cell));
    }

    private void onMouseDragged(MouseEvent event) {
        if (isCastlingSelected || isDeleteCellSelected) return;
        if (isSingleDragging) {
            final ObservableList<Node> pingerControllers = mainPane.getChildren();
            double moduleHeight = ((PingerController) pingerControllers.get(Utils.MAX_PINGER_GROUPS)).getPrefHeight();
            double delta = -(moduleHeight * GRID_ROW + spaceBetweenCells * GRID_ROW);
            if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
                if (!dustbinHBox.isVisible()) {
                    // that in case of somehow the trash not show and controller of dragging is not null
                    toggleBottomMenu(false);
                }
                restoreControllerVisibilityAndPosition(event, pingerControllers, draggedItem, delta);
                makeBorderOnHoverController(event, pingerControllers, draggedItem);
            }

            isOnTrash(event.getSceneX(), event.getSceneY());


        } else {
            final ObservableList<Node> pingerControllers = mainPane.getChildren();
            double moduleHeight = ((PingerController) pingerControllers.get(Utils.MAX_PINGER_GROUPS)).getPrefHeight();
            double delta = -(moduleHeight * GRID_ROW + spaceBetweenCells * GRID_ROW);
            for (PingerController pingerController : selectionList) {
                if (pingerController != null && pingerController.getState() != KorTypes.PingerGridItemState.EMPTY) {
                    restoreControllerVisibilityAndPosition(event, pingerControllers, pingerController, delta);
                    makeBorderOnHoverController(event, pingerControllers, pingerController);
                }
            }
        }
    }

    private void makeBorderOnHoverController(MouseEvent event, ObservableList<Node> pingerControllers, PingerController draggedItem) {
        PingerController itemControllerHover = findItemByCoordinates(event.getSceneX(), event.getSceneY(), true);
        if (itemControllerHover != null && !itemControllerHover.equals(draggedItem)) {
            if (!itemControllerHover.getStyleClass().contains("menu-item-hover")) {
                itemControllerHover.getStyleClass().add("menu-item-hover");
                colorItem = itemControllerHover;
            }
        } else {
            if (colorItem != null) {
                removeBorderFromAllNodes(pingerControllers);
                colorItem = null;
            }
        }
    }

    private void removeBorderFromAllNodes(ObservableList<Node> pingerControllers) {
        for (Node child : pingerControllers) {
            child.getStyleClass().remove("menu-item-hover");
        }
    }

    private void restoreControllerVisibilityAndPosition(MouseEvent event, ObservableList<Node> pingerControllers, PingerController draggedItem, double delta) {
        pingerControllers.get(draggedItem.getIndexOnGrid()).getStyleClass().remove("main-pane-not-empty");
        pingerControllers.get(draggedItem.getIndexOnGrid()).setVisible(false);

        pingerControllers.get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateY(delta + event.getSceneY() - y);
        pingerControllers.get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateX(event.getSceneX() - x);
    }


    private void onMouseReleased(MouseEvent event) {
        logger.debug("onMouseReleased");
        if (isDeleteCellSelected || isCastlingSelected) {
            logger.debug("consume event");
            return;
        }
        cancelLongPressTimer();
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

            if ((colorItem != null && colorItem.getState() != KorTypes.PingerGridItemState.DUMMY) && (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY && !draggedItem.isDeleted())) {
                colorItem.getStyleClass().remove("menu-item-hover");
                switchModules(draggedItem, colorItem);
            }
            removeBorderFromAllNodes(mainPane.getChildren());
            draggedItem = null;
            colorItem = null;

            dusbinImageView.setFitHeight(30);
            dusbinImageView.setFitWidth(30);


            ZoomOut zoomOut = new ZoomOut(dustbinHBox);
            zoomOut.setSpeed(3);
            zoomOut.setOnFinished(event1 -> {
                toggleBottomMenu(true);
                compressedTrash();
            });

            zoomOut.setResetOnFinished(true).play();
        } else {
            if (colorItem != null && colorItem.getState() != (KorTypes.PingerGridItemState.DUMMY)) {
                selectionList.sort(Comparator.comparingInt(PingerController::getIndexOnGrid));
                int targetIndex = colorItem.getIndexOnGrid();
                int delta = targetIndex - selectionList.get(0).getIndexOnGrid();
                for (PingerController currentController : selectionList) {
                    int index = currentController.getIndexOnGrid() + targetIndex;
                    if (index < 0 || index >= Utils.MAX_PINGER_GROUPS) {
                        AlertPopupController.sendAlert(KorTypes.AlertTypes.INFO, "Can't ", this);
                        return;
                    }
                    if (((PingerController) getGridCollection().get(currentController.getIndexOnGrid() + delta)).getState() == KorTypes.PingerGridItemState.ACTIVE) {
                        AlertPopupController.sendAlert(KorTypes.AlertTypes.INFO, "Can't there is others ", this);
                        return;
                    }
                }
                AlertPopupController.sendAlert(KorTypes.AlertTypes.INFO, "YEss ", this);
            }
        }

    }

    private void deleteItem(PingerController draggedItem) {
        logger.debug("deleteItem: {}", draggedItem.getFirstRow());
        draggedItem.setDeleted(true);
        PingerController emptyPingerController = PingerController.emptyPingerController();
        emptyPingerController.setIndexOnGrid(draggedItem.getIndexOnGrid());
        mainPane.getChildren().set(draggedItem.getIndexOnGrid(), emptyPingerController);
        Animation.zoomIn(emptyPingerController, 2);
    }

    private void switchModules(PingerController pinger1, PingerController pinger2) {
        logger.debug("switchModules between Indexes: {}<->{}", pinger2.getIndexOnGrid(), pinger1.getIndexOnGrid());
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(mainPane.getChildren());
        Collections.swap(workingCollection, pinger2.getIndexOnGrid(), pinger1.getIndexOnGrid());
        mainPane.getChildren().setAll(workingCollection);
        swapIndexOngGrid(pinger2, pinger1);
        Animation.zoomIn(pinger2, 2);
        Animation.zoomIn(pinger1, 2);
    }

    private void swapIndexOngGrid(PingerController ctl1, PingerController ctl2) {
        int temp = ctl1.getIndexOnGrid();
        ctl1.setIndexOnGrid(ctl2.getIndexOnGrid());
        ctl2.setIndexOnGrid(temp);
    }

    private void isOnTrash(double x, double y) {
        double startX, startY, endX, endY;
        startX = bottomMenu.getLayoutX();
        endX = startX + bottomMenu.getWidth();
        startY = bottomMenu.getLayoutY();
        endY = startY + bottomMenu.getHeight();

        if ((startX < x && endX > x) && (startY < y && endY > y)) {
            enlargeTrash();
        } else {
            compressedTrash();
        }
    }

    private void compressedTrash() {
        if (isTrashEnlarged) {
            tada.setResetOnFinished(true).stop();
            dusbinImageView.setRotate(0);
        }
        dusbinImageView.setFitHeight(30);
        dusbinImageView.setFitWidth(30);
        isTrashEnlarged = false;
        dustbinHBox.setOpacity(1);
        dustbinHBox.setStyle("-fx-background-color: #083C68;");
    }

    private void enlargeTrash() {
        if (!isTrashEnlarged) {
            tada.setCycleCount(AnimationFX.INDEFINITE).play();
        }

        isTrashEnlarged = true;
        dusbinImageView.setFitHeight(50);
        dusbinImageView.setFitWidth(50);
        dustbinHBox.setOpacity(0.5);
        dustbinHBox.setStyle("-fx-background-color: red;");
    }


    private PingerController findItemByCoordinates(double x, double y, boolean isForStyling) {
        double startX, startY, endX, endY;
        PingerController itemController;
        for (Node currentItemController : mainPane.getChildren()) {
            itemController = (PingerController) currentItemController;
            startX = currentItemController.getLayoutX();
            endX = startX + ((PingerController) currentItemController).getWidth();
            startY = currentItemController.getLayoutY();
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
        Scene scene = new Scene(this.loadView(), 750, 556);
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

    private ObservableList<Node> getGridCollection() {
        return mainPane.getChildren();
    }

}

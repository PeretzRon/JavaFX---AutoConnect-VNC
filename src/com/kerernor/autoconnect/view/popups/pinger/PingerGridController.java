package com.kerernor.autoconnect.view.popups.pinger;

import animatefx.animation.ZoomIn;
import com.kerernor.autoconnect.Main;
import com.kerernor.autoconnect.util.KorCommon;
import com.kerernor.autoconnect.util.KorTypes;
import com.kerernor.autoconnect.util.Utils;
import com.kerernor.autoconnect.view.PingGroupItemController;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PingerGridController extends AnchorPane {

    @FXML
    private FlowPane mainPane;

    @FXML
    private Button saveChanges;

    private Parent paneBehind;
    private Stage stage;
    private ObservableList<Node> items;
    private final Logger logger = LogManager.getLogger(PingerGridController.class);

    private double x, y;
    private PingerController draggedItem;
    private PingerController colorItem;
    private final int gridRows = 7;
    private final double spaceBetweenCells = 10d;

    public PingerGridController(ObservableList<Node> items, Parent paneBehind) {
        logger.debug("LIST SIZE: " + items.size());
        this.items = items;
        this.paneBehind = paneBehind;
    }

    public PingerGridController() {

    }

    @FXML
    public void initialize() {
        mainPane.setVgap(spaceBetweenCells);
        mainPane.setHgap(spaceBetweenCells);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        mainPane.setOrientation(Orientation.HORIZONTAL);
        System.out.println("Init Controller");
        for (int i = 0; i < items.size(); i++) {
            logger.debug(i);
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setFirstRow(((PingGroupItemController) items.get(i)).getName());
            pingerController.setState(KorTypes.PingerGridItemState.ACTIVE);
            pingerController.setPingGroupItemController(((PingGroupItemController) items.get(i)));
            pingerController.getStyleClass().setAll("main-pane-not-empty");
            mainPane.getChildren().add(pingerController);
        }

        for (int i = items.size(); i < Utils.MAX_PINGER_GROUPS; i++) {
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setState(KorTypes.PingerGridItemState.EMPTY);
            pingerController.getStyleClass().add("main-pane");
            mainPane.getChildren().add(pingerController);
        }

        for (int i = 0; i < Utils.MAX_PINGER_GROUPS; i++) {
            PingerController pingerController = new PingerController();
            pingerController.setIndexOnGrid(i);
            pingerController.setState(KorTypes.PingerGridItemState.EMPTY);
            pingerController.getStyleClass().add("main-pane");
            mainPane.getChildren().add(pingerController);
        }

        mainPane.setOnMousePressed(this::onMousePress);
        mainPane.setOnMouseDragged(this::onMouseDragged);
        mainPane.setOnMouseReleased(this::onMouseReleased);
        saveChanges.setOnMouseClicked(this::saveChangesHandler);
    }

    private void saveChangesHandler(MouseEvent event) {
        List<PingGroupItemController> list = new ArrayList<>();
        for (Node node : mainPane.getChildren()) {
            if (((PingerController) node).getState() != KorTypes.PingerGridItemState.EMPTY) {
                list.add(((PingerController) node).getPingGroupItemController());
            }
        }

        System.out.println(list);
        KorCommon.getInstance().getPingerScreenController().getFlowPaneGroupPinger().getChildren().setAll(list);
        closeClickAction();
    }


    private void onMousePress(MouseEvent event) {
        final PingerController item = findItemByCoordinates(event.getSceneX(), event.getSceneY(), false);
        double moduleHeight = ((PingerController) mainPane.getChildren().get(0)).getPrefHeight();
        double delta = -(moduleHeight * gridRows + spaceBetweenCells * gridRows);
        if (item != null && item.getState() != KorTypes.PingerGridItemState.EMPTY) {
            int indexOnGrid = item.getIndexOnGrid();
            ((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS)).setFirstRow(item.getFirstRow());
            mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("main-pane-not-empty");
            mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).getStyleClass().add("main-pane");
            scaleNode((PingerController) mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS), 1.1);
            mainPane.getChildren().get(indexOnGrid + Utils.MAX_PINGER_GROUPS).setTranslateY(delta);
            y = event.getSceneY();
            x = event.getSceneX();
        }
    }

    private void onMouseDragged(MouseEvent event) {
        double moduleHeight = ((PingerController) mainPane.getChildren().get(Utils.MAX_PINGER_GROUPS)).getPrefHeight();
        double delta = -(moduleHeight * gridRows + spaceBetweenCells * gridRows);

        if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
            mainPane.getChildren().get(draggedItem.getIndexOnGrid()).getStyleClass().remove("main-pane-not-empty");

            mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateY(delta + event.getSceneY() - y);
            mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateX(event.getSceneX() - x);

            PingerController itemControllerHover = findItemByCoordinates(event.getSceneX(), event.getSceneY(), true);
            if (itemControllerHover != null && !itemControllerHover.equals(draggedItem)) {
                if (!itemControllerHover.getStyleClass().contains("menu-item-hover")) {
                    itemControllerHover.getStyleClass().add("menu-item-hover");
                    colorItem = itemControllerHover;
                }
            } else {
                if (colorItem != null) {
                    colorItem.getStyleClass().remove("menu-item-hover");
                    colorItem = null;
                }
            }
        }
    }


    private void onMouseReleased(MouseEvent event) {
        if (draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
            mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateY(0);
            mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS).setTranslateX(0);
            mainPane.getChildren().get(draggedItem.getIndexOnGrid()).getStyleClass().add("main-pane-not-empty");
            scaleNode((PingerController) mainPane.getChildren().get(draggedItem.getIndexOnGrid() + Utils.MAX_PINGER_GROUPS), 1);
        }

        if (colorItem != null && draggedItem != null && draggedItem.getState() != KorTypes.PingerGridItemState.EMPTY) {
            colorItem.getStyleClass().remove("menu-item-hover");
            switchModules();
        }
        draggedItem = null;
    }

    private void switchModules() {
        ObservableList<Node> workingCollection = FXCollections.observableArrayList(mainPane.getChildren());
        Collections.swap(workingCollection, colorItem.getIndexOnGrid(), draggedItem.getIndexOnGrid());
        mainPane.getChildren().setAll(workingCollection);
        swapIndexOngGrid(colorItem, draggedItem);

        ZoomIn zoomIn = new ZoomIn(colorItem);
        zoomIn.setSpeed(3);
        zoomIn.play();

        ZoomIn zoomIn2 = new ZoomIn(draggedItem);
        zoomIn2.setSpeed(3);
        zoomIn2.play();
    }

    private void swapIndexOngGrid(PingerController ctl1, PingerController ctl2) {
        int temp = ctl1.getIndexOnGrid();
        ctl1.setIndexOnGrid(ctl2.getIndexOnGrid());
        ctl2.setIndexOnGrid(temp);
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
                System.out.println("FoundController " + itemController.getFirstRow());
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


    public AnchorPane loadView() {
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
        Scene scene = new Scene(this.loadView(), 780, 520);
        stage = new Stage();
        stage.setScene(scene);
//        stage.setResizable(false);
//        stage.initStyle(StageStyle.UNDECORATED);

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

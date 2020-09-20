package com.kerernor.autoconnect.util;

import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.stage.Stage;

public class Utils {

    // Base paths
    public static final String BASE_PATH = "com/kerernor/autoconnect/";
    public static final String VIEWS_BASE_PATH = "view/";
    public static final String IMAGES_BASE_PATH = BASE_PATH + "images/";
    public static final String VIEWS_POPUPS_BASE_PATH = VIEWS_BASE_PATH + "popups/";

    // View paths
    public static final String MAIN_VIEW = VIEWS_BASE_PATH + "main.fxml";
    public static final String COMPUTER_LIST = VIEWS_BASE_PATH + "computerList.fxml";
    public static final String COMPUTER_ROW_VIEW = VIEWS_BASE_PATH + "computerRow.fxml";
    public static final String SEARCH_AREA = VIEWS_BASE_PATH + "searchArea.fxml";
    public static final String CONFIRM_POPUP = VIEWS_POPUPS_BASE_PATH +  "confirmPopup.fxml";
    public static final String ALERT_POPUP = VIEWS_POPUPS_BASE_PATH +  "alertPopup.fxml";
    public static final String ADD_EDIT_COMPUTER_POPUP = VIEWS_POPUPS_BASE_PATH +  "addEditComputerPopup.fxml";
    public static final String REMOTE_SCREEN_VIEW = VIEWS_BASE_PATH +  "remoteScreenController.fxml";



    // Images file paths
    public static final String RCGW_ICON = IMAGES_BASE_PATH + "antenna.png" ;
    public static final String STATION_ICON = IMAGES_BASE_PATH + "stationKO.png" ;

    // Data paths
    public static final String COMPUTER_DATA = "data.json";


    // Sizes and amounts
    public static final int BLUR_SIZE = 5;
    public static final int BLUR_ITERATIONS = 3;

    // Text display
    public static final String TEXT_CONFIRM_DELETE_TITLE = "Deletion confirmation";
    public static final String TExT_CONFIRM_DELETE_COMPUTER_MESSAGE = "Are you sure to delete: ";
    public static final String TEXT_ADD_NEW_COMPUTER_POPUP = "Add new computer";
    public static final String TEXT_EDIT_COMPUTER_POPUP = "Edit ";
    public static final String WRONG_IP_ADDRESS_MASSAGE = "Wrong IP Address, try again";



    /**
     * This method gets the blur effect.
     * @return BoxBlur effect.
     */
    public static Effect getBlurEffect() {
        BoxBlur blurEffect = new BoxBlur();
        blurEffect.setWidth(BLUR_SIZE);
        blurEffect.setHeight(BLUR_SIZE);
        blurEffect.setIterations(BLUR_ITERATIONS);

        return blurEffect;
    }

    /**
     * This method gets empty effect.
     * @return empty effect (null).
     */
    public static Effect getEmptyEffect() {
        return null;
    }


    public static void centerNewStageToBehindStage(Parent paneBehind, Stage newStage) {
        // Calculate the center position of the parent Stage
        Stage primaryStage = (Stage) paneBehind.getScene().getWindow();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth()/2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight()/2d;

        newStage.setX(centerXPosition - newStage.getWidth()/2d);
        newStage.setY(centerYPosition - newStage.getHeight()/2d);
    }
}

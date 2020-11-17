package com.kerernor.autoconnect.util;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Utils {

    private static Logger logger = Logger.getLogger(Utils.class);
    public static Map<String, Image> appImages = new HashMap<>();

    // Base paths
    public static final String BASE_PATH = "com/kerernor/autoconnect/";
    public static final String VIEWS_BASE_PATH = "view/";
    public static final String IMAGES_BASE_PATH = BASE_PATH + "images/";
    public static final String VIEWS_COMPONENTS_BASE_PATH = VIEWS_BASE_PATH + "components/";
    public static final String VIEWS_POPUPS_BASE_PATH = VIEWS_BASE_PATH + "popups/";
    public static final String VIEWS_SCREENS_BASE_PATH = VIEWS_BASE_PATH + "screens/";
    public static final String CONFIG_BASE_PATH = BASE_PATH + "config/";

    // View paths
    public static final String MAIN_VIEW = VIEWS_BASE_PATH + "main.fxml";
    public static final String COMPUTER_LIST = VIEWS_BASE_PATH + "computerList.fxml";
    public static final String LAST_REMOTE_DRIVE_LIST = VIEWS_BASE_PATH + "lastRemoteDriveList.fxml";
    public static final String REMOTE_SCREEN = VIEWS_SCREENS_BASE_PATH + "remoteScreen.fxml";
    public static final String PINGER_SCREEN = VIEWS_SCREENS_BASE_PATH + "pingerScreen.fxml";
    public static final String REMOTE_DRIVE_SCREEN = VIEWS_SCREENS_BASE_PATH + "remoteDriveScreen.fxml";
    public static final String ABOUT_SCREEN = VIEWS_SCREENS_BASE_PATH + "AboutScreen.fxml";
    public static final String GUARD_SCREEN = VIEWS_BASE_PATH + "guard.fxml";
    public static final String COMPUTER_ROW_VIEW = VIEWS_BASE_PATH + "computerRow.fxml";
    public static final String LAST_REMOTE_DRIVE_ROW_VIEW = VIEWS_BASE_PATH + "lastRemoteDriveRow.fxml";
    public static final String SEARCH_AREA = VIEWS_BASE_PATH + "searchArea.fxml";
    public static final String PINGER_LIST = VIEWS_BASE_PATH + "pingListGroup.fxml";
    public static final String PINGER_ROW_VIEW = VIEWS_BASE_PATH + "pingRowItem.fxml";
    public static final String LAST_CONNECTION_ROW_VIEW = VIEWS_BASE_PATH + "LastConnectionRow.fxml";
    public static final String LAST_CONNECTION_LIST_VIEW = VIEWS_BASE_PATH + "LastConnectionList.fxml";
    public static final String LAST_CONNECTION_POPUP_VIEW = VIEWS_BASE_PATH + "LastConnectionsPopup.fxml";
    public static final String CONFIRM_POPUP = VIEWS_POPUPS_BASE_PATH + "confirmPopup.fxml";
    public static final String ALERT_POPUP = VIEWS_POPUPS_BASE_PATH + "alertPopup.fxml";
    public static final String ADD_EDIT_COMPUTER_POPUP = VIEWS_POPUPS_BASE_PATH + "addEditComputerPopup.fxml";
    public static final String ADD_EDIT_PINGER_ITEMS = VIEWS_POPUPS_BASE_PATH + "addEditPingerItems.fxml";
    public static final String PING_GROUP_ITEM = VIEWS_BASE_PATH + "pingGroupItem.fxml";
    public static final String J_TEXT_FIELD = VIEWS_COMPONENTS_BASE_PATH + "jTextField.fxml";

    // Images file paths
    public static final String RCGW_ICON = IMAGES_BASE_PATH + "antenna.png";
    public static final String STATION_ICON = IMAGES_BASE_PATH + "stationKO.png";
    public static final String APP_ICON = IMAGES_BASE_PATH + "ko.png";
    public static final String REMOTE_ICON = IMAGES_BASE_PATH + "remote-btn-icon.png";
    public static final String PINGER_ICON = IMAGES_BASE_PATH + "pinger-btn-icon.png";
    public static final String REMOTE_DRIVE_ICON = IMAGES_BASE_PATH + "remote-drive-btn-icon.png";
    public static final String ABOUT_ICON = IMAGES_BASE_PATH + "about-btn-icon.png";
    public static final String EXIT_ICON = IMAGES_BASE_PATH + "exit-btn-icon.png";
    public static final String ALERT_WARNING_ICON = IMAGES_BASE_PATH + "warning.png";
    public static final String ALERT_ERROR_ICON = IMAGES_BASE_PATH + "close-icon.png";

    // Data paths
    public static final String COMPUTER_DATA = "data/data.json";
    public static final String PINGER_DATA = "data/pingData.json";
    public static final String LAST_CONNECTIONS_HISTORY_DATA = "data/lastConnectionsHistory.json";

    // Config files paths
    public static final String LOG_4_J_CONFIG = "config/log4j.properties";
    public static final String APP_SETTINGS = "config/settings.properties";

    // Parameters
    public static String VNC_PROGRAM_PATH = "C:\\Program Files (x86)\\uvnc bvba\\UltraVNC";
    public static String VNC_SCRIPT_PATH = "C:\\Programs\\ControlKO\\vnc\\";
    public static boolean IS_REMOTE_DRIVE_SCREEN_ACTIVE = true;

    // App setting properties
    public static final String CONFIG_ULTRA_VNC_PROGRAM_PATH = "UltraVncProgramPath";
    public static final String CONFIG_ULTRA_VNC_SCRIPT_FOR_CONNECTION = "UltraVncScriptForConnection";
    public static final String CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS = "isPopupCloseIfLoseFocus";
    public static final String CONFIG_VERSION_NUMBER_PROPERTIES = "versionNumber";
    public static boolean CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING = true;
    public static String CONFIG_IS_REMOTE_DRIVE_SCREEN_ACTIVE = "isRemoteDriveScreenActive";


    // Sizes and amounts
    public static final int BLUR_SIZE = 5;
    public static final int BLUR_ITERATIONS = 3;
    public static final int MAX_HISTORY_CONNECT_LIST = 50;
    public static final int MAX_REMOTE_DRIVE_CONNECT_LIST = 50;
    public static final int TIME_FOR_CLOSE_POPUP = 7000;
    public static final int TIME_FOR_CLOSE_ALERT_MESSAGE_WARNING = 3500;
    public static final int TIME_FOR_CLOSE_ALERT_MESSAGE_ERROR = 6000;
    public static final long TIMEOUT_FOR_PROCESS_TO_END_IN_SECONDS = 20;

    // Text display
    public static final String TEXT_CONFIRM_DELETE_TITLE = "Deletion confirmation";
    public static final String TExT_CONFIRM_DELETE_COMPUTER_MESSAGE = "Are you sure to delete: ";
    public static final String TEXT_ADD_NEW_COMPUTER_POPUP = "Add new computer";
    public static final String TEXT_EDIT_COMPUTER_POPUP = "Edit ";
    public static final String TEXT_ADD_NEW_GROUP_PINGER_POPUP_TITTLE = "add new group Pinger";
    public static final String APP_NAME = "ControlKO";
    public static final String COPYRIGHT = "Copyright " + "\u00a9" + " Ron Peretz (2020)";
    public static String VERSION = "Version ";
    public static StringProperty VERSION_NUMBER = new SimpleStringProperty("1.0.1");

    // Alert Messages
    public static final String WRONG_IP_ADDRESS_MASSAGE = "Wrong ip address, try again";
    public static final String VNC_PATH_ERROR = "Ultra VNC not installed or path not valid";


    // Style
    public static final String PINGER_GROUP_ITEM_SELECTED = "pinger-item-selected";

    /**
     * This method gets the blur effect.
     *
     * @return BoxBlur effect.
     */
    public static Effect getBlurEffect() {
        logger.trace("Effect.getBlurEffect");
        BoxBlur blurEffect = new BoxBlur();
        blurEffect.setWidth(BLUR_SIZE);
        blurEffect.setHeight(BLUR_SIZE);
        blurEffect.setIterations(BLUR_ITERATIONS);

        return blurEffect;
    }

    /**
     * This method gets empty effect.
     *
     * @return empty effect (null).
     */
    public static Effect getEmptyEffect() {
        logger.trace("Effect.getEmptyEffect");
        return null;
    }


    public static void centerNewStageToBehindStage(Parent paneBehind, Stage newStage) {
        // Calculate the center position of the parent Stage
        logger.info("centerNewStageToBehindStage");
        Stage primaryStage = (Stage) paneBehind.getScene().getWindow();
        double centerXPosition = primaryStage.getX() + primaryStage.getWidth() / 2d;
        double centerYPosition = primaryStage.getY() + primaryStage.getHeight() / 2d;

        newStage.setX(centerXPosition - newStage.getWidth() / 2d);
        newStage.setY(centerYPosition - newStage.getHeight() / 2d);
    }

    public static boolean isValidateIpAddress(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public static boolean isNullOrEmptyString(final String str) {
        return str != null && str.length() == 0;
    }

    public static void loadAndSetLoggerSetting() {
        Properties props = new Properties();
        try (InputStream inputStream = new FileInputStream(LOG_4_J_CONFIG)) {
            props.load(inputStream);
            PropertyConfigurator.configure(props);
            logger.info("loadAndSetLoggerSetting");
        } catch (IOException e) {
            logger.error("failed to load log4j settings");
        }
    }

    public static void loadAppSettings() {
        ThreadManger.getInstance().getThreadPoolExecutor().execute(() -> {
            logger.info("loadAppSettings");
            Properties properties = new Properties();

            try (InputStream inputStream = new FileInputStream(APP_SETTINGS)) {
                properties.load(inputStream);
                Utils.VNC_PROGRAM_PATH = properties.getProperty(CONFIG_ULTRA_VNC_PROGRAM_PATH);
                Utils.VNC_SCRIPT_PATH = properties.getProperty(CONFIG_ULTRA_VNC_SCRIPT_FOR_CONNECTION);
                Utils.CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS_SETTING = Boolean.parseBoolean(properties.getProperty(CONFIG_IS_POPUP_CLOSE_IF_LOSE_FOCUS));
                Utils.IS_REMOTE_DRIVE_SCREEN_ACTIVE = Boolean.parseBoolean(properties.getProperty(CONFIG_IS_REMOTE_DRIVE_SCREEN_ACTIVE));
                Platform.runLater(() -> Utils.VERSION_NUMBER.set(properties.getProperty(Utils.CONFIG_VERSION_NUMBER_PROPERTIES)));
            } catch (IOException e) {
                logger.error("failed to load app settings");
            }
        });
    }

    public static void showStageOnTopAndWait(Stage stage) {
        stage.setAlwaysOnTop(true);
        stage.showAndWait();
        Platform.runLater(() -> stage.setAlwaysOnTop(false));
    }

    public static void onMouseClickNode(Node node) {
        Event.fireEvent(node, new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                0, 0, 0, MouseButton.PRIMARY, 1, true, true, true, true,
                true, true, true, true, true, true, null));
    }

    public static void loadImages() {
        appImages.put(Utils.RCGW_ICON, new Image(Utils.RCGW_ICON));
        appImages.put(Utils.STATION_ICON, new Image(Utils.STATION_ICON));
        appImages.put(Utils.REMOTE_ICON, new Image(Utils.REMOTE_ICON));
        appImages.put(Utils.PINGER_ICON, new Image(Utils.PINGER_ICON));
        appImages.put(Utils.REMOTE_DRIVE_ICON, new Image(Utils.REMOTE_DRIVE_ICON));
        appImages.put(Utils.ABOUT_ICON, new Image(Utils.ABOUT_ICON));
        appImages.put(Utils.EXIT_ICON, new Image(Utils.EXIT_ICON));
        appImages.put(Utils.ALERT_ERROR_ICON, new Image(Utils.ALERT_ERROR_ICON));
        appImages.put(Utils.ALERT_WARNING_ICON, new Image(Utils.ALERT_WARNING_ICON));
    }


    public static Image getImageByName(String name) {
        return appImages.computeIfAbsent(name, s -> new Image(name));
    }
}

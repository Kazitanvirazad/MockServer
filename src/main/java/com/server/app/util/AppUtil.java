package com.server.app.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDClock;
import com.server.app.config.AppConfig;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.IntegerRange;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.server.app.constants.ApplicationConstants.EMPTY_STRING;
import static com.server.app.constants.ApplicationConstants.HYPHEN;
import static com.server.app.constants.ApplicationConstants.SECURE_RANDOM_ALGORITHM;

/**
 * author: Kazi Tanvir Azad
 */
public final class AppUtil {
    private static final Logger log = LogManager.getLogger(AppUtil.class);
    public static final IntegerRange SERVER_PORT_RANGE = IntegerRange.of(1, 65535);
    public static final IntegerRange RESPONSE_CODE_RANGE = IntegerRange.of(100, 599);

    private AppUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static Window getApplicationWindowByTitle(String title) {
        return Stage.getWindows()
                .stream()
                .filter(Window::isShowing)
                .filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getTitle().contains(title))
                .findFirst()
                .orElse(null);
    }

    public static void bringExistingActiveWindowToFrontOrElse(Runnable OrElseAction, String... filterTitles) {
        Stage.getWindows()
                .stream()
                .filter(Window::isShowing)
                .filter(window -> window instanceof Stage)
                .filter(window -> {
                    for (String title : filterTitles) {
                        if (((Stage) window).getTitle().contains(title)) {
                            return true;
                        }
                    }
                    return false;
                })
                .findFirst().
                ifPresentOrElse(window -> {
                    Stage stage = (Stage) window;
                    stage.toFront();
                }, OrElseAction);
    }

    public static void closeWindowButtonEvent(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static void closeWindowWithTitle(String... titles) {
        try {
            ObservableList<Window> windows = Stage.getWindows();
            for (String title : titles) {
                closeWindowWithTitle(windows, title);
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    private static void closeWindowWithTitle(ObservableList<Window> windows, String title) {
        if (CollectionUtils.isNotEmpty(windows)) {
            Optional<Window> stageOptional = windows.stream()
                    .filter(ObjectUtils::isNotEmpty)
                    .filter(window -> title.equals(((Stage) window).getTitle()))
                    .findFirst();
            stageOptional.ifPresent(window -> ((Stage) window).close());
        }
    }

    public static void exitApplication() {
        exitApplication(null);
    }

    public static void exitApplication(Event event) {
        try {
            // Get all the opened, unclosed and active windows
            ObservableList<Window> windows = Stage.getWindows();
            // Closing all the opened, unclosed and active windows
            while (CollectionUtils.isNotEmpty(windows)) {
                ((Stage) windows.getLast()).close();
            }
            Platform.exit();
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    public static void triggerErrorAlert(String headerText, String contentText) {
        initializeAlert(headerText, contentText, AlertType.ERROR);
    }

    public static void triggerInfoAlert(String headerText, String contentText) {
        initializeAlert(headerText, contentText, AlertType.INFORMATION);
    }

    public static void triggerWarningAlert(String headerText, String contentText) {
        initializeAlert(headerText, contentText, AlertType.WARNING);
    }

    private static void initializeAlert(String headerText, String contentText, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setGraphic(new ImageView(AppConfig.INSTANCE.getAppLogo()));
        alert.setResizable(false);
        switch (alertType) {
            case AlertType.WARNING -> alert.setTitle("Warning");
            case AlertType.ERROR -> alert.setTitle("Error");
            case AlertType.INFORMATION -> alert.setTitle("Info");
        }
        alert.show();
    }

    public static ButtonType triggerConfirmationPrompt(String headerText, String contentText) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.setGraphic(new ImageView(AppConfig.INSTANCE.getAppLogo()));
        alert.setResizable(false);
        alert.setTitle("Choose to confirm");
        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(ButtonType.CANCEL);
    }

    public static Optional<String> generateUUID7BasedId() {
        Optional<String> uuid7 = generateUUID7();
        return uuid7.map(uuid7String -> uuid7String.replace(HYPHEN, EMPTY_STRING))
                .map(String::toUpperCase);
    }

    private static Optional<String> generateUUID7() {
        try {
            UUID uuid7 = Generators.timeBasedEpochRandomGenerator(getRandom(), UUIDClock.systemTimeClock()).generate();
            return Optional.ofNullable(uuid7.toString());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    private static Random getRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
    }

    public static int getRandomNumberInRange(int min, int max) throws NoSuchAlgorithmException {
        return getRandom().nextInt(max - min) + min;
    }
}

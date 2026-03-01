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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.server.app.constants.ApplicationConstants.EMPTY_STRING;
import static com.server.app.constants.ApplicationConstants.HYPHEN;
import static com.server.app.constants.ApplicationConstants.SECURE_RANDOM_ALGORITHM;

/**
 * @author Kazi Tanvir Azad
 */
public final class AppUtil {
    private static final Logger log = LogManager.getLogger(AppUtil.class);
    public static final IntegerRange SERVER_PORT_RANGE = IntegerRange.of(1, 65535);
    public static final IntegerRange RESPONSE_CODE_RANGE = IntegerRange.of(100, 599);

    private AppUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    /**
     * Finds the window with the provided title
     *
     * @param title {@link String}
     * @return {@link Window} with the provided title or {@code null} if no window found
     */
    public static Window getApplicationWindowByTitle(String title) {
        return Stage.getWindows()
                .stream()
                .filter(Window::isShowing)
                .filter(window -> window instanceof Stage)
                .filter(window -> ((Stage) window).getTitle().contains(title))
                .findFirst()
                .orElse(null);
    }

    /**
     * Brings the window to front if its already open to prevent opening same window multiple times. Or else performs
     * the {@link Runnable} action if there is no same window opened
     *
     * @param OrElseAction {@link Runnable} Action to perform if there is no window found with the provided titles
     * @param filterTitles {@link String} Array of titles to filter from all the opened windows
     */
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

    /**
     * Closes the stage bound with {@link Event} passed in the argument
     *
     * @param event {@link Event}
     */
    public static void closeWindowButtonEvent(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Closed all the stages with the specified titles passed in the argument
     *
     * @param titles {@link String} Array of titles
     */
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

    /**
     * Closed all the stages with the specified title passed in the argument
     *
     * @param windows {@link ObservableList}<{@link Window}>
     * @param title   {@link String} Title of the stage to be closed
     */
    private static void closeWindowWithTitle(ObservableList<Window> windows, String title) {
        if (CollectionUtils.isNotEmpty(windows)) {
            Optional<Window> stageOptional = windows.stream()
                    .filter(Objects::nonNull)
                    .filter(window -> title.equals(((Stage) window).getTitle()))
                    .findFirst();
            stageOptional.ifPresent(window -> ((Stage) window).close());
        }
    }

    /**
     * Closes all the opened window and performs application exit.
     * <br>{@link javafx.application.Application} stop method will get executed
     */
    public static void exitApplication() {
        exitApplication(null);
    }

    /**
     * Closes all the opened window and performs application exit.
     * <br>{@link javafx.application.Application} stop method will get executed
     *
     * @param event {@link Event}
     */
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

    /**
     * Opens a JavaFX Alert window with Error {@link AlertType}
     *
     * @param headerText  {@link String} Text to display in the header section of the Alert stage
     * @param contentText {@link String} Text to display in the context section of the Alert stage
     */
    public static void triggerErrorAlert(String headerText, String contentText) {
        initializeAlert(headerText, contentText, AlertType.ERROR);
    }

    /**
     * Opens a JavaFX Alert window with Information {@link AlertType}
     *
     * @param headerText  {@link String} Text to display in the header section of the Alert stage
     * @param contentText {@link String} Text to display in the context section of the Alert stage
     */
    public static void triggerInfoAlert(String headerText, String contentText) {
        initializeAlert(headerText, contentText, AlertType.INFORMATION);
    }

    /**
     * Opens a JavaFX Alert window with provided {@link AlertType}
     *
     * @param headerText  {@link String} Text to display in the header section of the Alert stage
     * @param contentText {@link String} Text to display in the context section of the Alert stage
     * @param alertType   The {@link AlertType} to be used
     */
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

    /**
     * Opens a JavaFX Alert Confirmation window for prompting user to choose one from OK and Cancel button
     *
     * @param headerText  {@link String} Text to display in the header section of the Alert stage
     * @param contentText {@link String} Text to display in the context section of the Alert stage
     * @return The {@link ButtonType} selected by the user
     */
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

    /**
     * Generates a unique alphanumeric key based on UUID version 7.
     * This method first generates UUID version 7, then it removes the hyphen and converts the string to upper case.
     *
     * @return {@link Optional}<{@link String}> Returns an Optional of unique alphanumeric key based on UUID version 7
     */
    public static Optional<String> generateUUID7BasedId() {
        Optional<String> uuid7 = generateUUID7();
        return uuid7.map(uuid7String -> uuid7String.replace(HYPHEN, EMPTY_STRING))
                .map(String::toUpperCase);
    }

    /**
     * Generates UUID version 7
     *
     * @return Returns {@link UUID} in {@link Optional}<{@link String}> format
     */
    private static Optional<String> generateUUID7() {
        try {
            UUID uuid7 = Generators.timeBasedEpochRandomGenerator(getRandom(), UUIDClock.systemTimeClock()).generate();
            return Optional.ofNullable(uuid7.toString());
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Returns a SecureRandom object that implements the 'SHA1PRNG' algorithm
     *
     * @return {@link Random}
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a {@code SecureRandomSpi}
     *                                  implementation for the specified algorithm
     */
    private static Random getRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance(SECURE_RANDOM_ALGORITHM);
    }

    /**
     * Securely generates a random number within the given range
     *
     * @param min {@code int} Minimum number in the range
     * @param max {@code int} Maximum number in the range
     * @return {@code int} Random number within the given range
     * @throws NoSuchAlgorithmException if no {@code Provider} supports a {@code SecureRandomSpi}
     *                                  implementation for the specified algorithm
     */
    public static int getRandomNumberInRange(int min, int max) throws NoSuchAlgorithmException {
        return getRandom().nextInt(max - min) + min;
    }
}

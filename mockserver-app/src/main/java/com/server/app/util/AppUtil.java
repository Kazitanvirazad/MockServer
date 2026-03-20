package com.server.app.util;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import static com.server.app.constants.AppConstants.ENV_PROPERTY_FILE_PATH;

/**
 * @author Kazi Tanvir Azad
 */
public final class AppUtil {
    private static final Logger log = LogManager.getLogger(AppUtil.class);

    private AppUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    /**
     * Finds the window with the provided title<br>
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
     * Brings the window to front if its already open to prevent opening same window multiple times.<br> Or else performs
     * the {@link Runnable} action if there is no same window opened<br>
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
     * Closes the stage bound with {@link Event} passed in the argument<br>
     *
     * @param event {@link Event}
     */
    public static void closeWindowButtonEvent(Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    /**
     * Closed all the stages with the specified titles passed in the argument<br>
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
     * Closed all the stages with the specified title passed in the argument<br>
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
     * Closes all the opened window and performs application exit.<br>
     * {@link javafx.application.Application} stop method will get executed
     */
    public static void exitApplication() {
        exitApplication(null);
    }

    /**
     * Closes all the opened window and performs application exit.
     * <br>{@link javafx.application.Application} stop method will get executed<br>
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
     * Add key press event handler for active Scene
     *
     * @param stage {@link Stage}
     * @param scene {@link Scene}
     */
    public static void setCloseWindowOnEscapeButtonPress(Stage stage, Scene scene) {
        if (Objects.nonNull(stage) && Objects.nonNull(scene)) {
            scene.setOnKeyPressed(keyEvent -> {
                if (CustomKeyCode.INSTANCE.getEscapeKeycode().equals(keyEvent.getCode())) {
                    stage.close();
                }
            });
        }
    }

    /**
     * Load properties from the specified property file present in the classpath<br>
     *
     * @return {@link Properties} Loaded properties from the env.properties file
     */
    public static Properties loadEnvironmentProperties() {
        Properties envProperties = new Properties();
        InputStream inputStream = AppUtil.class.getResourceAsStream(ENV_PROPERTY_FILE_PATH);
        if (null == inputStream) {
            return envProperties;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            envProperties.load(reader);
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return envProperties;
    }
}

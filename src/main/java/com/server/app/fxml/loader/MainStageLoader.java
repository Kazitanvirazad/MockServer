package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.MainAppController;
import com.server.app.exception.StageLoadException;
import com.server.app.util.AppUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.server.app.constants.ApplicationConstants.APP_TITLE;

/**
 * author: Kazi Tanvir Azad
 */
public class MainStageLoader implements StageLoader<MainAppController> {
    private static final Logger log = LogManager.getLogger(MainStageLoader.class);
    private final Stage stage;
    private final FXMLLoader fxmlLoader;

    public MainStageLoader(Stage stage) {
        this.stage = stage;
        this.fxmlLoader = new FXMLLoader(MainStageLoader.class.getResource("main-app.fxml"));
    }

    @Override
    public void loadStage() {
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setTitle(APP_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.setMinWidth(1225.0);
            stage.setMinHeight(740.0);
            stage.setOnHidden(AppUtil::exitApplication);
            stage.show();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public MainAppController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

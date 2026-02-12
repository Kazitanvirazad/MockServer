package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.SplashScreenController;
import com.server.app.exception.StageLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.server.app.constants.ApplicationConstants.SPLASH_SCREEN_TITLE;

public class SplashScreenStageLoader implements StageLoader<SplashScreenController> {
    private static final Logger log = LogManager.getLogger(SplashScreenStageLoader.class);
    private final FXMLLoader fxmlLoader;
    private final Stage stage;

    public SplashScreenStageLoader() {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(SplashScreenStageLoader.class.getResource("splashscreen.fxml"));
    }

    @Override
    public void loadStage() {
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(SPLASH_SCREEN_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public SplashScreenController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

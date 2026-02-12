package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.HeaderFormController;
import com.server.app.exception.StageLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.server.app.constants.ApplicationConstants.APP_HEADER_FORM_TITLE;

public class HeaderFormStageLoader implements StageLoader<HeaderFormController> {
    private static final Logger log = LogManager.getLogger(HeaderFormStageLoader.class);
    private final FXMLLoader fxmlLoader;
    private final Stage stage;

    public HeaderFormStageLoader() {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(HeaderFormStageLoader.class.getResource("header-form.fxml"));
    }

    @Override
    public void loadStage() {
        try {
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(APP_HEADER_FORM_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.showAndWait();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public HeaderFormController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

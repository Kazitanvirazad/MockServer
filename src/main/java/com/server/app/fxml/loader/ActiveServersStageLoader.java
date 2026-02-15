package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.ActiveServersController;
import com.server.app.exception.StageLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import static com.server.app.constants.ApplicationConstants.ACTIVE_SERVER_MANAGER_TITLE;

/**
 * author: Kazi Tanvir Azad
 */
public class ActiveServersStageLoader implements StageLoader<ActiveServersController> {
    private static final Logger log = LogManager.getLogger(ActiveServersStageLoader.class);
    private final URL location = CollectionFormStageLoader.class.getResource("active-servers.fxml");
    private final FXMLLoader fxmlLoader;
    private final Stage stage;

    public ActiveServersStageLoader() {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(location);
    }

    @Override
    public void loadStage() {
        try {
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(ACTIVE_SERVER_MANAGER_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public ActiveServersController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

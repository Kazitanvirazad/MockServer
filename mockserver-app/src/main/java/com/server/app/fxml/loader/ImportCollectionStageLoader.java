package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.ImportCollectionController;
import com.server.app.exception.StageLoadException;
import com.server.app.util.CustomKeyCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.server.app.constants.AppConstants.APP_IMPORT_COLLECTION_TITLE;

/**
 * @author Kazi Tanvir Azad
 */
public class ImportCollectionStageLoader implements StageLoader<ImportCollectionController> {
    private static final Logger log = LogManager.getLogger(ImportCollectionStageLoader.class);
    private final FXMLLoader fxmlLoader;
    private final Stage stage;

    public ImportCollectionStageLoader() {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(ImportCollectionStageLoader.class.getResource("import-collection.fxml"));
    }

    @Override
    public void loadStage() {
        try {
            Scene scene = new Scene(fxmlLoader.load());
            // Adding key press event handler for import server Scene
            scene.setOnKeyPressed(keyEvent -> {
                if (CustomKeyCode.INSTANCE.getEscapeKeycode().equals(keyEvent.getCode())) {
                    stage.close();
                }
            });
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(APP_IMPORT_COLLECTION_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.showAndWait();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public ImportCollectionController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

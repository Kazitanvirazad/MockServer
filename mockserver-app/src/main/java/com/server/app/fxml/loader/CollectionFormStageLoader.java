package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.CollectionFormController;
import com.server.app.exception.StageLoadException;
import com.server.app.util.CustomKeyCode;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import static com.server.app.constants.AppConstants.APP_COLLECTION_FORM_TITLE;
import static com.server.app.constants.AppConstants.APP_EDIT_COLLECTION_FORM_TITLE;

/**
 * @author Kazi Tanvir Azad
 */
public class CollectionFormStageLoader implements StageLoader<CollectionFormController> {
    private static final Logger log = LogManager.getLogger(CollectionFormStageLoader.class);
    private final URL location = CollectionFormStageLoader.class.getResource("collection-form.fxml");
    private final FXMLLoader fxmlLoader;
    private final Stage stage;
    private final boolean doEdit;
    private final String collectionName;

    public CollectionFormStageLoader(boolean doEdit, String collectionName) {
        this.doEdit = doEdit;
        this.collectionName = collectionName;
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(location);
    }

    @Override
    public void loadStage() {
        try {
            Parent root = fxmlLoader.load();
            getController().setDoEdit(doEdit);
            getController().setCollectionName(collectionName);
            getController().initialize(location, null);
            Scene scene = new Scene(root);
            // Adding key press event handler for add collection form Scene
            scene.setOnKeyPressed(keyEvent -> {
                if (CustomKeyCode.INSTANCE.getEscapeKeycode().equals(keyEvent.getCode())) {
                    stage.close();
                }
            });
            stage.setScene(scene);
            stage.setResizable(false);
            if (this.doEdit) stage.setTitle(APP_EDIT_COLLECTION_FORM_TITLE);
            else stage.setTitle(APP_COLLECTION_FORM_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.showAndWait();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public CollectionFormController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.ServerFormController;
import com.server.app.exception.StageLoadException;
import com.server.app.model.data.Collection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;

import static com.server.app.constants.ApplicationConstants.APP_COOKIE_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_HEADER_FORM_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_EDIT_BUTTON;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_EDIT_TITLE;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_SAVE_BUTTON;
import static com.server.app.constants.ApplicationConstants.APP_SERVER_FORM_TITLE;
import static com.server.app.util.AppUtil.closeWindowWithTitle;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerFormStageLoader implements StageLoader<ServerFormController> {
    private static final Logger log = LogManager.getLogger(ServerFormStageLoader.class);
    private final URL location = ServerFormStageLoader.class.getResource("server-form.fxml");
    private Collection selectedCollection;
    private final FXMLLoader fxmlLoader;
    private final Stage stage;
    private final String title;
    private final String saveServerButtonText;
    private String serverId;

    public ServerFormStageLoader(String serverId, Collection selectedCollection) {
        this(APP_SERVER_FORM_EDIT_BUTTON, APP_SERVER_FORM_EDIT_TITLE);
        this.serverId = serverId;
        this.selectedCollection = selectedCollection;
    }

    private ServerFormStageLoader(String saveServerButtonText, String title) {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(location);
        this.saveServerButtonText = saveServerButtonText;
        this.title = title;
    }

    public ServerFormStageLoader() {
        this(APP_SERVER_FORM_SAVE_BUTTON, APP_SERVER_FORM_TITLE);
    }

    public ServerFormStageLoader(Collection selectedCollection) {
        this(APP_SERVER_FORM_SAVE_BUTTON, APP_SERVER_FORM_TITLE);
        this.selectedCollection = selectedCollection;
    }

    @Override
    public void loadStage() {
        try {
            Parent root = fxmlLoader.load();
            if (null != this.selectedCollection) {
                getController().setDefaultSelectedCollection(this.selectedCollection);
            }
            if (null != this.serverId) {
                getController().setServerId(this.serverId);
            }
            getController().setMockServerFormTitle(this.title);
            getController().setSaveServerButton(this.saveServerButtonText);
            getController().initialize(location, null);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(this.title);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.setOnHidden(event -> {
                closeWindowWithTitle(APP_COOKIE_FORM_TITLE, APP_HEADER_FORM_TITLE);
            });
            stage.showAndWait();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public ServerFormController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

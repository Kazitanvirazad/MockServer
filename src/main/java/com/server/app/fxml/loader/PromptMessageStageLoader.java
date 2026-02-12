package com.server.app.fxml.loader;

import com.server.app.config.AppConfig;
import com.server.app.controller.PromptMessageController;
import com.server.app.exception.StageLoadException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static com.server.app.constants.ApplicationConstants.APP_PROMPT_MESSAGE_TITLE;

public class PromptMessageStageLoader implements StageLoader<PromptMessageController> {
    private static final Logger log = LogManager.getLogger(PromptMessageStageLoader.class);
    private String promptMessage;
    private final FXMLLoader fxmlLoader;
    private final Stage stage;

    private PromptMessageStageLoader() {
        this.stage = new Stage();
        this.fxmlLoader = new FXMLLoader(PromptMessageStageLoader.class.getResource("prompt-message.fxml"));
    }

    public PromptMessageStageLoader(String promptMessage) {
        this();
        this.promptMessage = promptMessage;
    }

    @Override
    public void loadStage() {
        try {
            Parent root = fxmlLoader.load();
            getController().setMessageText(this.promptMessage);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle(APP_PROMPT_MESSAGE_TITLE);
            stage.getIcons().add(AppConfig.INSTANCE.getAppLogo());
            stage.show();
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new StageLoadException(exception);
        }
    }

    @Override
    public PromptMessageController getController() {
        return fxmlLoader.getController();
    }

    @Override
    public Stage getStage() {
        return stage;
    }
}

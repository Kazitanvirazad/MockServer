package com.server.app.fxml.loader;

import javafx.stage.Stage;

/**
 * @author Kazi Tanvir Azad
 */
public interface StageLoader<T> {
    void loadStage();

    T getController();

    Stage getStage();
}

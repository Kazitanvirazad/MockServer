package com.server.app.fxml.loader;

import javafx.stage.Stage;

public interface StageLoader<T> {
    void loadStage();

    T getController();

    Stage getStage();
}

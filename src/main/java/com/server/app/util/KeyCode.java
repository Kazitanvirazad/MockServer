package com.server.app.util;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

public enum KeyCode {
    INSTANCE;
    private final KeyCodeCombination COPY_KEYCODE_COMBINATION;

    public KeyCodeCombination getCopyKeycodeCombination() {
        return COPY_KEYCODE_COMBINATION;
    }

    {
        this.COPY_KEYCODE_COMBINATION = new KeyCodeCombination(javafx.scene.input.KeyCode.C, KeyCombination.CONTROL_ANY);
    }
}

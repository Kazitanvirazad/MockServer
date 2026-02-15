package com.server.app.util;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

/**
 * author: Kazi Tanvir Azad
 */
public enum CustomKeyCode {
    INSTANCE;
    private final KeyCodeCombination COPY_KEYCODE_COMBINATION;
    private final KeyCode ESCAPE_KEYCODE;
    private final KeyCode ENTER_KEYCODE;
    private final KeyCode DELETE_KEYCODE;

    public KeyCodeCombination getCopyKeycodeCombination() {
        return COPY_KEYCODE_COMBINATION;
    }

    public KeyCode getEscapeKeycode() {
        return ESCAPE_KEYCODE;
    }

    public KeyCode getEnterKeycode() {
        return ENTER_KEYCODE;
    }

    public KeyCode getDeleteKeycode() {
        return DELETE_KEYCODE;
    }

    {
        this.COPY_KEYCODE_COMBINATION = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
        this.ESCAPE_KEYCODE = KeyCode.ESCAPE;
        this.ENTER_KEYCODE = KeyCode.ENTER;
        this.DELETE_KEYCODE = KeyCode.DELETE;
    }
}

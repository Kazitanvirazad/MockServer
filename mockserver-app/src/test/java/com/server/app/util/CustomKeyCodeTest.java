package com.server.app.util;

import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomKeyCodeTest {
    @Test
    void singletonExposesPlatformAwareKeyCodes() {
        assertNotNull(CustomKeyCode.INSTANCE.getCopyKeycodeCombination());
        assertEquals(KeyCode.ESCAPE, CustomKeyCode.INSTANCE.getEscapeKeycode());
        assertEquals(KeyCode.ENTER, CustomKeyCode.INSTANCE.getEnterKeycode());
        assertEquals(SystemUtils.IS_OS_MAC ? KeyCode.BACK_SPACE : KeyCode.DELETE,
                CustomKeyCode.INSTANCE.getDeleteKeycode());
    }
}

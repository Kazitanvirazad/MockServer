package com.server.core.util;

/**
 * @author Kazi Tanvir Azad
 */
public interface Notification {
    void triggerInfoNotification(String headerText, String contentText);

    void triggerErrorNotification(String headerText, String contentText);

    boolean triggerConfirmationPrompt(String headerText, String contentText);
}
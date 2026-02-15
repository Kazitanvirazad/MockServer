package com.server.app.controller;

import com.server.app.model.data.Header;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.server.app.util.AppUtil.closeWindowButtonEvent;
import static com.server.app.util.AppUtil.triggerErrorAlert;

/**
 * author: Kazi Tanvir Azad
 */
public class HeaderFormController {
    private static final Logger log = LogManager.getLogger(HeaderFormController.class);
    @FXML
    private TextField headerKeyInput;
    @FXML
    private TextField headerValueInput;
    @FXML
    private Button saveHeaderButton;
    @FXML
    private Button cancelButton;
    private Header headerInput;

    @FXML
    private void handleSubmitButtonEvent(Event event) {
        var key = headerKeyInput.getText();
        var value = headerValueInput.getText();
        if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
            try {
                headerInput = new Header(key.trim(), value.trim());
            } catch (RuntimeException exception) {
                headerInput = null;
            }
            closeWindowButtonEvent(event);
        } else {
            triggerErrorAlert("Invalid Header Input!", "Continue to add Key and Value to the Header");
        }
    }

    @FXML
    private void handleCancelButtonEvent(Event event) {
        closeWindowButtonEvent(event);
    }

    // Get Header form input
    public Optional<Header> getHeaderInput() {
        return Optional.ofNullable(headerInput);
    }
}
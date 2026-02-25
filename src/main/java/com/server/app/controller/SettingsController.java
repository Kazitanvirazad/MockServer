package com.server.app.controller;

import com.server.app.config.AppConfig;
import com.server.app.service.Service;
import com.server.app.service.SettingsService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;

import java.net.URL;
import java.util.ResourceBundle;

import static com.server.app.util.AppUtil.closeWindowButtonEvent;

/**
 * @author Kazi Tanvir Azad
 */
public class SettingsController implements Initializable {
    private final SettingsService settingsService;

    public SettingsController() {
        this.settingsService = Service.INSTANCE.getSettingsService();
    }

    @FXML
    private CheckBox startServerOnStartupCheck;

    @FXML
    private void handleSaveSettingsButton(ActionEvent event) {
        boolean isStartServerOnStartupCheck = startServerOnStartupCheck.isSelected();

        // setting all the values to configuration
        AppConfig.INSTANCE.getConfiguration().setStartServerOnStartup(isStartServerOnStartupCheck);

        // updating configuration
        settingsService.updateConfig();
        closeWindowButtonEvent(event);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting the 'Start Server On Startup Checkbox' persisted value
        startServerOnStartupCheck.setSelected(AppConfig.INSTANCE.getConfiguration().isStartServerOnStartup());
    }
}

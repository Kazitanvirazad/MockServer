package com.server.app.service;

import com.server.app.repository.Repository;
import com.server.app.repository.SettingsRepository;

/**
 * @author Kazi Tanvir Azad
 */
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService() {
        this.settingsRepository = Repository.INSTANCE.getSettingsRepository();
    }

    /**
     * Syncs the settings from the database to the {@link com.server.app.model.data.Configuration} in the
     * <br>{@link com.server.app.config.AppConfig} singleton
     */
    public void syncConfig() {
        settingsRepository.syncConfiguration();
    }

    /**
     * Updated the setting from {@link com.server.app.model.data.Configuration} in the
     * <br>{@link com.server.app.config.AppConfig} singleton to the database
     */
    public void updateConfig() {
        settingsRepository.updateConfiguration();
    }

    /**
     * Created the default settings if no settings data exists and puts to database and syncs the
     * <br>settings from the database to the {@link com.server.app.model.data.Configuration} in the
     * <br>{@link com.server.app.config.AppConfig} singleton
     */
    public void initAndSyncSettings() {
        int rowCount = settingsRepository.getRowCount();
        if (rowCount == 0) {
            settingsRepository.initSettingsTable();
        }
        syncConfig();
    }
}

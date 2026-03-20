package com.server.app.service;

import com.server.app.model.data.Configuration;
import com.server.app.repository.AppRepository;
import com.server.app.repository.SettingsRepository;
import com.server.core.config.CommonConfig;

/**
 * @author Kazi Tanvir Azad
 */
public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService() {
        this.settingsRepository = AppRepository.INSTANCE.getSettingsRepository();
    }

    /**
     * Syncs the settings from the database to the {@link Configuration} in the
     * <br>{@link CommonConfig} singleton
     */
    public void syncConfig() {
        settingsRepository.syncConfiguration();
    }

    /**
     * Updated the setting from {@link Configuration} in the
     * <br>{@link CommonConfig} singleton to the database
     */
    public void updateConfig() {
        settingsRepository.updateConfiguration();
    }

    /**
     * Created the default settings if no settings data exists and puts to database and syncs the
     * <br>settings from the database to the {@link Configuration} in the
     * <br>{@link CommonConfig} singleton
     */
    public void initAndSyncSettings() {
        int rowCount = settingsRepository.getRowCount();
        if (rowCount == 0) {
            settingsRepository.initSettingsTable();
        }
        syncConfig();
    }
}

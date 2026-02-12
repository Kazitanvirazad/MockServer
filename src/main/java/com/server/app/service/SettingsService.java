package com.server.app.service;

import com.server.app.repository.Repository;
import com.server.app.repository.SettingsRepository;

public class SettingsService {
    private final SettingsRepository settingsRepository;

    public SettingsService() {
        this.settingsRepository = Repository.INSTANCE.getSettingsRepository();
    }

    public void syncConfig() {
        settingsRepository.syncConfiguration();
    }

    public void updateConfig() {
        settingsRepository.updateConfiguration();
    }

    public void initAndSyncSettings() {
        int rowCount = settingsRepository.getRowCount();
        if (rowCount == 0) {
            settingsRepository.initSettingsTable();
        }
        syncConfig();
    }
}

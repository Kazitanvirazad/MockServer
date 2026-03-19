package com.server.app.repository;

/**
 * @author Kazi Tanvir Azad
 */
public enum AppRepository {
    INSTANCE;
    private final ServerRestartRepository serverRestartRepository;
    private final SettingsRepository settingsRepository;

    public ServerRestartRepository getServerRestartRepository() {
        return serverRestartRepository;
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    {
        this.serverRestartRepository = new ServerRestartRepository();
        this.settingsRepository = new SettingsRepository();
    }
}

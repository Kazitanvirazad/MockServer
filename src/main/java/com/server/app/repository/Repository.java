package com.server.app.repository;

public enum Repository {
    INSTANCE;
    private final CollectionRepository collectionRepository;
    private final ServerRepository serverRepository;
    private final ServerRestartRepository serverRestartRepository;
    private final SettingsRepository settingsRepository;

    public CollectionRepository getCollectionRepository() {
        return collectionRepository;
    }

    public ServerRepository getServerRepository() {
        return serverRepository;
    }

    public ServerRestartRepository getServerRestartRepository() {
        return serverRestartRepository;
    }

    public SettingsRepository getSettingsRepository() {
        return settingsRepository;
    }

    {
        this.collectionRepository = new CollectionRepository();
        this.serverRepository = new ServerRepository();
        this.serverRestartRepository = new ServerRestartRepository();
        this.settingsRepository = new SettingsRepository();
    }
}

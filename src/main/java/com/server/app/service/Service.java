package com.server.app.service;

import javafx.application.HostServices;

public enum Service {
    INSTANCE;
    private final CollectionService collectionService;
    private final ServerService serverService;
    private final ServerRestartService serverRestartService;
    private final SettingsService settingsService;
    private HostServices hostServices;

    public CollectionService getCollectionService() {
        return collectionService;
    }

    public ServerService getServerService() {
        return serverService;
    }

    public ServerRestartService getServerRestartService() {
        return serverRestartService;
    }

    public SettingsService getSettingsService() {
        return settingsService;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void openUrlInBrowser(String url) {
        this.hostServices.showDocument(url);
    }

    {
        this.collectionService = new CollectionService();
        this.serverService = new ServerService();
        this.serverRestartService = new ServerRestartService();
        this.settingsService = new SettingsService();
    }
}

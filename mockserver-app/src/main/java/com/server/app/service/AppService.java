package com.server.app.service;

import javafx.application.HostServices;

/**
 * @author Kazi Tanvir Azad
 */
public enum AppService {
    INSTANCE;
    private final TableDataService tableDataService;
    private final ServerRestartService serverRestartService;
    private final SettingsService settingsService;
    private HostServices hostServices;

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

    public TableDataService getTableDataService() {
        return tableDataService;
    }

    {
        this.tableDataService = new TableDataService();
        this.serverRestartService = new ServerRestartService();
        this.settingsService = new SettingsService();
    }
}

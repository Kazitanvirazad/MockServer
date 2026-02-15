package com.server.app.config;

import com.server.app.model.data.Configuration;
import com.server.app.util.ImportExportUtil;
import javafx.scene.image.Image;
import tools.jackson.databind.ObjectMapper;

/**
 * author: Kazi Tanvir Azad
 */
public enum AppConfig {
    INSTANCE;
    private final Image appLogo;
    private final Image splashScreenLogo;
    private final ObjectMapper mapper;
    private final Configuration configuration;
    private final ImportExportUtil ioUtil;

    public Image getAppLogo() {
        return appLogo;
    }

    public Image getSplashScreenLogo() {
        return splashScreenLogo;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ImportExportUtil getIoUtil() {
        return ioUtil;
    }

    {
        this.appLogo = new Image("/static/icons/mockface-40.png");
        this.splashScreenLogo = new Image("/static/icons/mockface-100.png");
        this.mapper = new ObjectMapper();
        this.configuration = new Configuration();
        this.ioUtil = new ImportExportUtil();
    }
}

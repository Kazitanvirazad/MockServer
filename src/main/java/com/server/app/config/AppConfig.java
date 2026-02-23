package com.server.app.config;

import com.server.app.model.data.Configuration;
import com.server.app.util.ImportExportUtil;
import javafx.scene.image.Image;
import org.apache.commons.lang3.SystemProperties;
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
        String appLogoPath = String.format("%1$sstatic%1$sicons%1$smockface-40.png", SystemProperties.getFileSeparator());
        String splashScreenLogoPath = String.format("%1$sstatic%1$sicons%1$smockface-100.png", SystemProperties.getFileSeparator());
        this.appLogo = new Image(appLogoPath);
        this.splashScreenLogo = new Image(splashScreenLogoPath);
        this.mapper = new ObjectMapper();
        this.configuration = new Configuration();
        this.ioUtil = new ImportExportUtil();
    }
}

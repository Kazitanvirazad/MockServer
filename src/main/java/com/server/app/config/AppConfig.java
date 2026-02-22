package com.server.app.config;

import com.server.app.model.data.Configuration;
import com.server.app.util.ImportExportUtil;
import javafx.scene.image.Image;
import tools.jackson.databind.ObjectMapper;

import static com.server.app.constants.ApplicationConstants.FILE_SEPARATOR;

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
        String appLogoPath = String.format("%sstatic%sicons%smockface-40.png", FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);
        String splashScreenLogoPath = String.format("%sstatic%sicons%smockface-100.png", FILE_SEPARATOR, FILE_SEPARATOR, FILE_SEPARATOR);
        this.appLogo = new Image(appLogoPath);
        this.splashScreenLogo = new Image(splashScreenLogoPath);
        this.mapper = new ObjectMapper();
        this.configuration = new Configuration();
        this.ioUtil = new ImportExportUtil();
    }
}

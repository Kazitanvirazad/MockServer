package com.server.app.config;

import com.server.app.model.data.Configuration;
import com.server.app.util.ImportExportUtil;
import javafx.scene.image.Image;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.Properties;

/**
 * @author Kazi Tanvir Azad
 */
public enum AppConfig {
    INSTANCE;
    private final Image appLogo;
    private final Image splashScreenLogo;
    private final ObjectMapper mapper;
    private final Configuration configuration;
    private final ImportExportUtil ioUtil;
    private Properties envProperties;

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

    public void setEnvProperties(Properties envProperties) {
        this.envProperties = envProperties;
    }

    public Optional<String> getEnvProperty(String name) {
        return Optional.ofNullable(name)
                .map(key -> this.envProperties.getProperty(key))
                .map(String::trim);
    }

    {
        this.appLogo = new Image("/static/icons/mockface-40.png");
        this.splashScreenLogo = new Image("/static/icons/mockface-100.png");
        this.mapper = new ObjectMapper();
        this.configuration = new Configuration();
        this.ioUtil = new ImportExportUtil();
    }
}

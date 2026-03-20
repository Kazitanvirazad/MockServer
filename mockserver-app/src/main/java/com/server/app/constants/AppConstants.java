package com.server.app.constants;

import org.apache.commons.lang3.SystemProperties;

/**
 * @author Kazi Tanvir Azad
 */
public class AppConstants {
    private AppConstants() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static final String SPLASH_SCREEN_TITLE = "Starting Mock Server....";
    public static final String APP_TITLE = "Mock Server";
    public static final String APP_HEADER_FORM_TITLE = "Add Header";
    public static final String APP_EXPORT_COLLECTION_TITLE = "Export Collection";
    public static final String APP_IMPORT_COLLECTION_TITLE = "Import Collection";
    public static final String APP_ACTIVE_SERVER_MANAGER_TITLE = "Active Server Manager";
    public static final String APP_SETTING_TITLE = "Settings";
    public static final String APP_COOKIE_FORM_TITLE = "Add Cookie";
    public static final String APP_COLLECTION_FORM_TITLE = "Add Collection";
    public static final String APP_EDIT_COLLECTION_FORM_TITLE = "Edit Collection";
    public static final String APP_SERVER_FORM_TITLE = "Create Mock Server";
    public static final String APP_SERVER_FORM_EDIT_TITLE = "Edit Mock Server";
    public static final String APP_SERVER_FORM_SAVE_BUTTON = "Create Server";
    public static final String APP_SERVER_FORM_EDIT_BUTTON = "Update Server";
    public static final String TRACER = "tracer";
    public static final String JAVA_CROSS_PLATFORM_USER_DIRECTORY_PATH = SystemProperties.getUserHome();
    public static final String LIGHT_GREY_COLOR_HEX_CODE = "#e3e3e3;";
    public static final String LIGHT_RED_COLOR_HEX_CODE = "#fcc6c2;";
    public static final String LIGHT_GREEN_COLOR_HEX_CODE = "#cafadb;";
    public static final String DELETE_BUTTON_IMAGE_PATH = "/static/icons/delete-24.png";
    public static final String ENV_PROPERTY_FILE_PATH = "/properties/env.properties";
}

package com.server.core.config;

import com.server.core.util.ImportExportUtil;
import com.server.core.util.Notification;
import tools.jackson.databind.ObjectMapper;

/**
 * @author Kazi Tanvir Azad
 */
public enum CommonConfig {
    INSTANCE;
    private Notification notification;
    private final ObjectMapper mapper;

    private final ImportExportUtil ioUtil;

    public ObjectMapper getMapper() {
        return mapper;
    }

    public ImportExportUtil getIoUtil() {
        return ioUtil;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Notification notification() {
        return notification;
    }

    {
        this.mapper = new ObjectMapper();
        this.ioUtil = new ImportExportUtil();
    }
}

package com.server.app.model.data;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Kazi Tanvir Azad
 */
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class Configuration implements Serializable {
    @Serial
    private static final long serialVersionUID = -3867642136358142262L;
    private boolean startServerOnStartup;

    public boolean isStartServerOnStartup() {
        return startServerOnStartup;
    }

    public void setStartServerOnStartup(boolean startServerOnStartup) {
        this.startServerOnStartup = startServerOnStartup;
    }

    public void updateConfiguration(Configuration configuration) {
        this.startServerOnStartup = configuration.isStartServerOnStartup();
    }
}

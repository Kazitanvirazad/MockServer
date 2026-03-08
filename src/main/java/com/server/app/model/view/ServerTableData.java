package com.server.app.model.view;

import com.server.app.model.data.Server;
import javafx.beans.property.SimpleObjectProperty;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerTableData implements TableData {
    private SimpleObjectProperty<Server> serverObjectProperty;

    public ServerTableData(SimpleObjectProperty<Server> serverObjectProperty) {
        this.serverObjectProperty = serverObjectProperty;
    }

    public Server getServerObjectProperty() {
        return serverObjectProperty.get();
    }

    public SimpleObjectProperty<Server> serverObjectPropertyProperty() {
        return serverObjectProperty;
    }

    public void setServerObjectProperty(Server serverObjectProperty) {
        this.serverObjectProperty.set(serverObjectProperty);
    }

    @Override
    public String getClipboardData() {
        return serverObjectProperty.get().toString();
    }
}

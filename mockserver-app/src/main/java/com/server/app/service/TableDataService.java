package com.server.app.service;

import com.server.app.model.view.CollectionTableData;
import com.server.app.model.view.ServerTableData;
import com.server.core.model.data.Collection;
import com.server.core.model.data.Server;
import com.server.core.service.CollectionService;
import com.server.core.service.ServerService;
import com.server.core.service.Service;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kazi Tanvir Azad
 */
public class TableDataService {
    private final CollectionService collectionService;
    private final ServerService serverService;

    public TableDataService() {
        this.collectionService = Service.INSTANCE.getCollectionService();
        this.serverService = Service.INSTANCE.getServerService();
    }

    /**
     * Returns the {@link List} of {@link ServerTableData} for the given collection
     *
     * @param collectionId {@link String}
     * @return {@link List} of {@link Server} for the given collectionId
     */
    public List<ServerTableData> getServerTableDataList(String collectionId) {
        return serverService.getServersByCollection(collectionId)
                .map(server -> new ServerTableData(new SimpleObjectProperty<>(server)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all the existing {@link Collection} from database and creates the {@link CollectionTableData}
     * with the collection data and return the {@link List}
     *
     * @return {@link List} of {@link CollectionTableData}
     */
    public List<CollectionTableData> getCollectionTableData() {
        return collectionService.getCollectionStream()
                .map(collection -> new CollectionTableData(new SimpleObjectProperty<>(collection)))
                .collect(Collectors.toList());
    }
}

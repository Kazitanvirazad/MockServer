package com.server.core.service;

/**
 * @author Kazi Tanvir Azad
 */
public enum Service {
    INSTANCE;
    private final CollectionService collectionService;
    private final ServerService serverService;

    public CollectionService getCollectionService() {
        return collectionService;
    }

    public ServerService getServerService() {
        return serverService;
    }

    {
        this.collectionService = new CollectionService();
        this.serverService = new ServerService();
    }
}

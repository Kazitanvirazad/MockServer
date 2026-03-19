package com.server.core.repository;

/**
 * @author Kazi Tanvir Azad
 */
public enum Repository {
    INSTANCE;
    private final CollectionRepository collectionRepository;
    private final ServerRepository serverRepository;

    public CollectionRepository getCollectionRepository() {
        return collectionRepository;
    }

    public ServerRepository getServerRepository() {
        return serverRepository;
    }

    {
        this.collectionRepository = new CollectionRepository();
        this.serverRepository = new ServerRepository();
    }
}

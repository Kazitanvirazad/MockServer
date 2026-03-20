package com.server.core.service;

import com.server.core.config.CommonConfig;
import com.server.core.model.data.Collection;
import com.server.core.repository.CollectionRepository;
import com.server.core.repository.Repository;

import java.util.Optional;
import java.util.stream.Stream;

import static com.server.core.util.CommonUtil.generateUniqueAlphanumericId;

/**
 * @author Kazi Tanvir Azad
 */
public class CollectionService {
    private final CollectionRepository collectionRepository;

    public CollectionService() {
        this.collectionRepository = Repository.INSTANCE.getCollectionRepository();
    }

    /**
     * Returns the {@link Optional} of {@link Collection} for the given collectionId
     *
     * @param collectionId {@link String}
     * @return {@link Optional} of {@link Collection} for the given collectionId
     */
    public Optional<Collection> getCollectionById(String collectionId) {
        return collectionRepository.getCollectionById(collectionId);
    }

    /**
     * Returns the {@link Optional} of {@link Collection} for the given collectionName
     *
     * @param collectionName {@link String}
     * @return {@link Optional} of {@link Collection} for the given collectionName
     */
    public Optional<Collection> getCollectionByName(String collectionName) {
        return collectionRepository.getCollectionByName(collectionName);
    }

    /**
     * Returns all the existing {@link Collection} stream from database
     *
     * @return {@link Stream} of {@link Collection}
     */
    public Stream<Collection> getCollectionStream() {
        return collectionRepository.getCollectionStream().sorted();
    }

    /**
     * Creates a new collection with the provided collection name in the database and returns the
     * newly created {@link Optional} of {@link Collection}
     *
     * @param collectionName {@link String}
     * @return the newly created {@link Optional} of {@link Collection}
     */
    public Optional<Collection> createCollection(String collectionName) {
        Optional<String> uid = generateUniqueAlphanumericId();
        if (uid.isEmpty()) {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Something went wrong while saving collection!", "Please try again later.");
            return Optional.empty();
        }
        int status = collectionRepository.createCollection(uid.get(), collectionName);
        if (status > 0) {
            CommonConfig.INSTANCE.notification()
                    .triggerInfoNotification("Collection Saved!", collectionName + " saved successfully.");
            return getCollectionById(uid.get());
        } else {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Failed to save collection!", "Please try again later.");
        }
        return Optional.empty();
    }

    /**
     * Creates new {@link Collection} with the given collection data passed in the argument
     *
     * @param collection {@link Collection} data to be used for creating the new collection
     * @return {@link Optional} of newly created collection id or empty if failed to create
     * @apiNote This method creates the {@link Collection} silently without triggering any Alert in case of failure
     */
    public Optional<String> createImportedCollection(Collection collection) {
        Optional<String> uid = generateUniqueAlphanumericId();
        if (uid.isEmpty()) {
            return Optional.empty();
        }
        int status = collectionRepository.createCollection(uid.get(), collection);
        if (status > 0) {
            return uid;
        }
        return Optional.empty();
    }

    /**
     * Modifies the name of the {@link Collection} with given collectionId
     *
     * @param collectionId   {@link String} collection id of the {@link Collection} which will get modified
     * @param collectionName {@link String} The new name to replace the existing name
     * @return {@link Optional} of modified {@link Collection} or empty if failed to modify
     */
    public Optional<Collection> editCollection(String collectionId, String collectionName) {
        int status = collectionRepository.editCollection(collectionId, collectionName);
        if (status > 0) {
            CommonConfig.INSTANCE.notification()
                    .triggerInfoNotification("Collection Updated!", collectionName + " updated successfully.");
            return getCollectionById(collectionId);
        } else {
            CommonConfig.INSTANCE.notification()
                    .triggerErrorNotification("Failed to update collection!", "Please try again later.");
        }
        return Optional.empty();
    }

    /**
     * Deletes the {@link Collection} with the given collection id from the database if exist
     *
     * @param collectionId {@link String} collection id of the {@link Collection} which will get deleted if exist
     * @return {@code boolean} True if successfully deletes the collection or False otherwise
     */
    public boolean deleteCollectionById(String collectionId) {
        int status = collectionRepository.deleteCollection(collectionId);
        return status > 0;
    }
}

package com.server.app.service;

import com.server.app.model.data.Collection;
import com.server.app.model.view.CollectionTableData;
import com.server.app.repository.CollectionRepository;
import com.server.app.repository.Repository;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.server.app.util.AppUtil.generateUUID7BasedId;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.AppUtil.triggerInfoAlert;

/**
 * @author Kazi Tanvir Azad
 */
public class CollectionService {
    private final CollectionRepository collectionRepository;

    public CollectionService() {
        this.collectionRepository = Repository.INSTANCE.getCollectionRepository();
    }

    public Optional<Collection> getCollectionById(String collectionId) {
        return collectionRepository.getCollectionById(collectionId);
    }

    public Optional<Collection> getCollectionByName(String collectionName) {
        return collectionRepository.getCollectionByName(collectionName);
    }

    public Stream<Collection> getCollectionStream() {
        return collectionRepository.getCollectionStream().sorted();
    }

    public List<CollectionTableData> getCollectionTableData() {
        return getCollectionStream()
                .map(collection -> new CollectionTableData(new SimpleObjectProperty<>(collection)))
                .collect(Collectors.toList());
    }

    public Optional<Collection> createCollection(String collectionName) {
        Optional<String> uid = generateUUID7BasedId();
        if (uid.isEmpty()) {
            triggerErrorAlert("Something went wrong while saving collection!", "Please try again later.");
            return Optional.empty();
        }
        int status = collectionRepository.createCollection(uid.get(), collectionName);
        if (status > 0) {
            triggerInfoAlert("Collection Saved!", collectionName + " saved successfully.");
            return getCollectionById(uid.get());
        } else {
            triggerErrorAlert("Failed to save collection!", "Please try again later.");
        }
        return Optional.empty();
    }

    public Optional<String> createImportedCollection(Collection collection) {
        Optional<String> uid = generateUUID7BasedId();
        if (uid.isEmpty()) {
            return Optional.empty();
        }
        int status = collectionRepository.createCollection(uid.get(), collection);
        if (status > 0) {
            return uid;
        }
        return Optional.empty();
    }

    public Optional<Collection> editCollection(String collectionId, String collectionName) {
        int status = collectionRepository.editCollection(collectionId, collectionName);
        if (status > 0) {
            triggerInfoAlert("Collection Updated!", collectionName + " updated successfully.");
            return getCollectionById(collectionId);
        } else {
            triggerErrorAlert("Failed to update collection!", "Please try again later.");
        }
        return Optional.empty();
    }

    public boolean deleteCollectionById(String collectionId) {
        int status = collectionRepository.deleteCollection(collectionId);
        return status > 0;
    }
}

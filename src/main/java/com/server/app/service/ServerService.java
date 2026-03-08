package com.server.app.service;

import com.server.app.model.data.Server;
import com.server.app.model.view.ServerTableData;
import com.server.app.repository.Repository;
import com.server.app.repository.ServerRepository;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.server.app.constants.ApplicationConstants.EMPTY_STRING;
import static com.server.app.util.AppUtil.generateUUID7BasedId;
import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.AppUtil.triggerInfoAlert;
import static com.server.app.util.Serializer.serializeList;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerService {
    private final ServerRepository serverRepository;

    public ServerService() {
        this.serverRepository = Repository.INSTANCE.getServerRepository();
    }

    /**
     * Returns the {@link Optional} of {@link Server} for the given serverId
     *
     * @param serverId {@link String}
     * @return {@link Optional} of {@link Server} for the given serverId
     */
    public Optional<Server> getServerById(String serverId) {
        return serverRepository.getServerById(serverId);
    }

    /**
     * Returns the {@link Stream} of {@link Server} for the given collection
     *
     * @param collectionId {@link String}
     * @return {@link Stream} of {@link Server} for the given collectionId
     */
    public Stream<Server> getServersByCollection(String collectionId) {
        return serverRepository.getServersByCollectionStream(collectionId).sorted();
    }

    /**
     * Returns the {@link List} of {@link ServerTableData} for the given collection
     *
     * @param collectionId {@link String}
     * @return {@link List} of {@link Server} for the given collectionId
     */
    public List<ServerTableData> getServerTableDataList(String collectionId) {
        return getServersByCollection(collectionId)
                .map(server -> new ServerTableData(new SimpleObjectProperty<>(server)))
                .collect(Collectors.toList());
    }

    /**
     * Creates new server with the given server data passed in the argument
     *
     * @param server {@link Server} data to be used for creating the new server
     * @return {@link Optional} of newly created {@link Server} or empty if failed to create
     */
    public Optional<Server> createServer(Server server) {
        if (null == server) {
            return Optional.empty();
        }
        Optional<String> uid = generateUUID7BasedId();
        if (uid.isEmpty()) {
            triggerErrorAlert("Something went wrong while creating Server!",
                    "Please try again later.");
            throw new RuntimeException("UUID7 generate failed while creating Server Id!");
        }
        server.setServerId(uid.get());
        Optional<String> headerJsonOptional = serializeList(server.getHeaders());
        Optional<String> cookieJsonOptional = serializeList(server.getCookies());
        int status = serverRepository.createServer(server,
                headerJsonOptional.orElse(EMPTY_STRING), cookieJsonOptional.orElse(EMPTY_STRING));
        if (status > 0) {
            triggerInfoAlert("Server Saved!", server.getServerName() + " saved successfully.");
            return getServerById(uid.get());
        } else {
            triggerErrorAlert("Failed to save server!", "Please try again later.");
        }
        return Optional.empty();
    }

    /**
     * Creates new {@link Server} in the provided {@link com.server.app.model.data.Collection}
     * <br>with the given server data passed in the argument
     *
     * @param server       {@link Server} data to be used for creating the new server
     * @param collectionId {@link String} id of the {@link com.server.app.model.data.Collection} in which the {@link Server}
     *                     will get added
     * @return {@link Optional} of newly created {@link Server} or empty if failed to create
     * @apiNote This method creates the {@link Server} silently without triggering any Alert in case of failure
     */
    public Optional<String> createImportedServer(Server server, String collectionId) {
        if (null == server) {
            return Optional.empty();
        }
        Optional<String> uid = generateUUID7BasedId();
        if (uid.isEmpty()) {
            return Optional.empty();
        }
        Optional<String> headerJsonOptional = serializeList(server.getHeaders());
        Optional<String> cookieJsonOptional = serializeList(server.getCookies());
        int status = serverRepository.createServer(server, headerJsonOptional.orElse(EMPTY_STRING),
                cookieJsonOptional.orElse(EMPTY_STRING), collectionId, uid.get());
        if (status > 0) {
            return uid;
        }
        return Optional.empty();
    }

    /**
     * Updated the existing {@link Server} with the new server data passed in the argument
     *
     * @param server {@link Server} data to be used for modifying the existing server
     * @return {@link Optional} of updated {@link Server} or empty if failed to update
     */
    public Optional<Server> updateServer(Server server) {
        if (null == server) {
            return Optional.empty();
        }
        Optional<String> headerJsonOptional = serializeList(server.getHeaders());
        Optional<String> cookieJsonOptional = serializeList(server.getCookies());
        int status = serverRepository.updateServer(server,
                headerJsonOptional.orElse(EMPTY_STRING), cookieJsonOptional.orElse(EMPTY_STRING));
        if (status > 0) {
            triggerInfoAlert("Server Updated!", server.getServerName() + " updated successfully.");
            return getServerById(server.getServerId());
        } else {
            triggerErrorAlert("Failed to update server!", "Please try again later.");
        }
        return Optional.empty();
    }

    /**
     * Deletes the {@link Server} with the given server id from the database if exist
     *
     * @param ServerId to be used to delete the existing server
     * @return {@code boolean} True if successfully deletes the server or False otherwise
     */
    public boolean deleteServerById(String ServerId) {
        int status = serverRepository.deleteServer(ServerId);
        return status > 0;
    }
}

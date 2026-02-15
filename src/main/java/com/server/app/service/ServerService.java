package com.server.app.service;

import com.server.app.model.data.Server;
import com.server.app.model.view.ServerTableData;
import com.server.app.repository.Repository;
import com.server.app.repository.ServerRepository;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.StringUtils;

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
 * author: Kazi Tanvir Azad
 */
public class ServerService {
    private final ServerRepository serverRepository;

    public ServerService() {
        this.serverRepository = Repository.INSTANCE.getServerRepository();
    }

    public Optional<Server> getServerById(String serverId) {
        Server server = serverRepository.getServerById(serverId);
        return StringUtils.isNotBlank(server.getServerId())
                && StringUtils.isNotBlank(server.getServerName()) ?
                Optional.of(server) : Optional.empty();
    }

    public Stream<Server> getServersByCollection(String collectionId) {
        return serverRepository.getServersByCollectionStream(collectionId).sorted();
    }

    public List<ServerTableData> getServerTableDataList(String collectionId) {
        return getServersByCollection(collectionId)
                .map(server -> new ServerTableData(new SimpleObjectProperty<>(server)))
                .collect(Collectors.toList());
    }

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

    public boolean deleteServerById(String ServerId) {
        int status = serverRepository.deleteServer(ServerId);
        return status > 0;
    }
}

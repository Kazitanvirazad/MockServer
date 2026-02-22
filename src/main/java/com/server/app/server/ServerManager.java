package com.server.app.server;

import com.server.app.model.data.Server;
import com.server.app.service.ServerService;
import com.server.app.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.server.app.util.AppUtil.triggerErrorAlert;
import static com.server.app.util.AppUtil.triggerInfoAlert;

/**
 * author: Kazi Tanvir Azad
 */
public enum ServerManager {
    INSTANCE;
    private final ObservableSet<String> activeServerIds;
    private final Map<Integer, ServerInitiator> activeServers;
    private final ServerService serverService;

    public void startServer(Server server, boolean silent) {
        // execute basic validations
        if (ObjectUtils.isEmpty(server)
                || StringUtils.isBlank(server.getServerId())
                || StringUtils.isBlank(server.getUrlEndpoint())) {
            if (!silent) {
                triggerErrorAlert("Invalid Server",
                        """
                                Invalid Server selection.
                                Try again later.""");
            }
            return;
        }
        if (isServerActive(server.getServerId())) {
            if (!silent) {
                triggerErrorAlert("Server is already running",
                        """
                                This server is already running.
                                Try staring another server.""");
            }
            return;
        }
        try {
            // If already server with same port is running
            if (activeServers.containsKey(server.getPort())) {
                ServerInitiator existinogServerInitiator = activeServers.getOrDefault(server.getPort(), null);
                if (ObjectUtils.isNotEmpty(existinogServerInitiator)) {
                    existinogServerInitiator.addEndpoint(server, activeServerIds);
                    existinogServerInitiator.restartServer();
                }
            } else {
                ServerInitiator serverInitiator = new ServerInitiator(server.getPort());
                serverInitiator.addEndpoint(server, activeServerIds);
                serverInitiator.startServer();
                activeServers.put(server.getPort(), serverInitiator);
            }
            activeServerIds.add(server.getServerId());
            if (!silent) {
                triggerInfoAlert("Server started", String.format("Server: %s is active", server.getServerName()));
            }
        } catch (Exception ignore) {
        }
    }

    public void stopServer(Server server, boolean silent) {
        // execute basic validations
        if (ObjectUtils.isEmpty(server)
                || StringUtils.isBlank(server.getServerId())
                || StringUtils.isBlank(server.getUrlEndpoint())) {
            if (!silent) {
                triggerErrorAlert("Invalid Server",
                        """
                                Invalid Server selection.
                                Try again later.""");
            }
            return;
        }
        if (!isServerActive(server.getServerId())) {
            if (!silent) {
                triggerErrorAlert("Server is not running",
                        """
                                This server is not running.
                                Try stopping a server already running.""");
            }
            return;
        }
        try {
            // getting already running server with same port
            ServerInitiator existinogServerInitiator = activeServers.getOrDefault(server.getPort(), null);
            if (ObjectUtils.isNotEmpty(existinogServerInitiator)) {
                existinogServerInitiator.removeEndpoint(server);
                activeServerIds.remove(server.getServerId());
                if (existinogServerInitiator.isServerStopped()) {
                    activeServers.remove(server.getPort());
                }
                if (!silent) {
                    triggerInfoAlert("Server stopped", String.format("Server: %s is inactive", server.getServerName()));
                }
            }
        } catch (Exception ignore) {
        }
    }

    public boolean isServerActive(String serverId) {
        return activeServerIds.contains(serverId);
    }

    {
        this.activeServers = new HashMap<>();
        this.activeServerIds = FXCollections.observableSet();
        this.serverService = Service.INSTANCE.getServerService();
    }

    public ObservableSet<String> getActiveServerIds() {
        return activeServerIds;
    }

    public boolean hasAnyActiveServer() {
        return CollectionUtils.isNotEmpty(activeServerIds);
    }

    public List<String> stopAllServers(boolean silent) {
        List<Server> activeServers = activeServerIds.stream()
                .filter(ObjectUtils::isNotEmpty)
                .map(serverService::getServerById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
        return activeServers.stream()
                .map(server -> {
                    stopServer(server, silent);
                    return server.getServerId();
                })
                .toList();
    }
}

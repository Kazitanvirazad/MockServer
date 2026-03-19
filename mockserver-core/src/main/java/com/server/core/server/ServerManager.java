package com.server.core.server;

import com.server.core.config.CommonConfig;
import com.server.core.model.data.Server;
import com.server.core.service.ServerService;
import com.server.core.service.Service;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Kazi Tanvir Azad
 */
public enum ServerManager {
    INSTANCE;
    private final ObservableSet<String> activeServerIds;
    private final Map<Integer, ServerInitiator> activeServers;
    private final ServerService serverService;

    /**
     * Starts the given server in the method argument or overrides the server which has same port number,
     * <br>url endpoint and http method
     *
     * @param server {@link Server} to start if it's already not running or override the similar server
     * @param silent {@code boolean} Flags if the operation will be processed silently, which means if it's set to
     *               <br>true then no JavaFX Alert will be triggered in case of any failures and exceptions
     *               <br>and if it's set to false then Alerts will be triggered in case of failure and exceptions
     * @apiNote overriding the existing server will happen after taking user's consent by triggering a confirmation Alert
     */
    public void startServer(Server server, boolean silent) {
        // execute basic validations
        if (Objects.isNull(server)
                || StringUtils.isBlank(server.getServerId())
                || StringUtils.isBlank(server.getUrlEndpoint())) {
            if (!silent) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Invalid Server",
                                """
                                        Invalid Server selection.
                                        Try again later.""");
            }
            return;
        }
        if (isServerActive(server.getServerId())) {
            if (!silent) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Server is already running",
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
                if (Objects.nonNull(existinogServerInitiator)) {
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
                CommonConfig.INSTANCE.notification()
                        .triggerInfoNotification("Server started", String.format("Server: %s is active", server.getServerName()));
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Stops the given server in the method argument if it's already running
     *
     * @param server {@link Server} to be stopped if it's already running
     * @param silent {@code boolean} Flags if the operation will be processed silently, which means if it's set to
     *               <br>true then no JavaFX Alert will be triggered in case of any failures and exceptions
     *               <br>and if it's set to false then Alerts will be triggered in case of failure and exceptions
     */
    public void stopServer(Server server, boolean silent) {
        // execute basic validations
        if (Objects.isNull(server)
                || StringUtils.isBlank(server.getServerId())
                || StringUtils.isBlank(server.getUrlEndpoint())) {
            if (!silent) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Invalid Server",
                                """
                                        Invalid Server selection.
                                        Try again later.""");
            }
            return;
        }
        if (!isServerActive(server.getServerId())) {
            if (!silent) {
                CommonConfig.INSTANCE.notification()
                        .triggerErrorNotification("Server is not running",
                                """
                                        This server is not running.
                                        Try stopping a server already running.""");
            }
            return;
        }
        try {
            // getting already running server with same port
            ServerInitiator existinogServerInitiator = activeServers.getOrDefault(server.getPort(), null);
            if (Objects.nonNull(existinogServerInitiator)) {
                existinogServerInitiator.removeEndpoint(server);
                activeServerIds.remove(server.getServerId());
                if (existinogServerInitiator.isServerStopped()) {
                    activeServers.remove(server.getPort());
                }
                if (!silent) {
                    CommonConfig.INSTANCE.notification()
                            .triggerInfoNotification("Server stopped", String.format("Server: %s is inactive", server.getServerName()));
                }
            }
        } catch (Exception ignore) {
        }
    }

    /**
     * Checks if the server with the provided id in the argument is active
     *
     * @param serverId {@link String} Server id to used for checking if it's active
     * @return {@code boolean} True if the server with the provided id in the argument is active or False otherwise
     */
    public boolean isServerActive(String serverId) {
        return activeServerIds.contains(serverId);
    }

    {
        this.activeServers = new HashMap<>();
        this.activeServerIds = FXCollections.observableSet();
        this.serverService = Service.INSTANCE.getServerService();
    }

    /**
     * Returns a {@link ObservableSet} of all the active servers
     *
     * @return {@link ObservableSet} of all the active servers
     */
    public ObservableSet<String> getActiveServerIds() {
        return activeServerIds;
    }

    /**
     * Checks whether any server is active
     *
     * @return True if there is any currently active server, False otherwise
     */
    public boolean hasAnyActiveServer() {
        return CollectionUtils.isNotEmpty(activeServerIds);
    }

    /**
     * Stops all the currently active servers and returns their id in the {@link List}
     *
     * @param silent {@code boolean} Flags if the operation will be processed silently, which means if it's set to
     *               <br>true then no JavaFX Alert will be triggered in case of any failures and exceptions
     *               <br>and if it's set to false then Alerts will be triggered in case of failure and exceptions
     * @return {@link List} of server ids which are made to stop
     */
    public List<String> stopAllServers(boolean silent) {
        List<Server> activeServers = activeServerIds.stream()
                .filter(StringUtils::isNotBlank)
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

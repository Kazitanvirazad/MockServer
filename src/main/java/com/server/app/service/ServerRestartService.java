package com.server.app.service;

import com.server.app.repository.Repository;
import com.server.app.repository.ServerRestartRepository;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Kazi Tanvir Azad
 */
public class ServerRestartService {
    private final ServerRestartRepository serverRestartRepository;

    public ServerRestartService() {
        this.serverRestartRepository = Repository.INSTANCE.getServerRestartRepository();
    }

    /**
     * Deletes all the data from server_restart table
     */
    public void resetServerRestartData() {
        serverRestartRepository.deleteServerRestartData();
    }

    /**
     * Returns all the server id's from the server_restart table
     *
     * @return {@link Stream} of server id's from the server_restart table
     */
    public Stream<String> getAllServerRestartDataStream() {
        return serverRestartRepository.getAllServerRestartDataStream();
    }

    /**
     * Adds all the server id's provided in the argument in to the server_restart table
     *
     * @param serverIds {@link List} of server id's
     */
    public void putServerRestartData(List<String> serverIds) {
        serverRestartRepository.insertServerRestartData(serverIds);
    }
}

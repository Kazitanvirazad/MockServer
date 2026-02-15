package com.server.app.service;

import com.server.app.repository.Repository;
import com.server.app.repository.ServerRestartRepository;

import java.util.List;
import java.util.stream.Stream;

/**
 * author: Kazi Tanvir Azad
 */
public class ServerRestartService {
    private final ServerRestartRepository serverRestartRepository;

    public ServerRestartService() {
        this.serverRestartRepository = Repository.INSTANCE.getServerRestartRepository();
    }

    public void resetServerRestartData() {
        serverRestartRepository.deleteServerRestartData();
    }

    public Stream<String> getAllServerRestartDataStream() {
        return serverRestartRepository.getAllServerRestartDataStream();
    }

    public void putServerRestartData(List<String> serverIds) {
        serverRestartRepository.insertServerRestartData(serverIds);
    }
}

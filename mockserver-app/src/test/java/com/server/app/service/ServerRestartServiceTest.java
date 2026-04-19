package com.server.app.service;

import com.server.app.repository.ServerRestartRepository;
import com.server.app.support.ReflectionTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServerRestartServiceTest {
    @Mock
    private ServerRestartRepository serverRestartRepository;

    private ServerRestartService serverRestartService;

    @BeforeEach
    void setUp() {
        serverRestartService = new ServerRestartService();
        ReflectionTestUtil.setField(serverRestartService, "serverRestartRepository", serverRestartRepository);
    }

    @Test
    void resetServerRestartDataDelegatesToRepository() {
        serverRestartService.resetServerRestartData();

        verify(serverRestartRepository).deleteServerRestartData();
    }

    @Test
    void getAllServerRestartDataStreamDelegatesToRepository() {
        when(serverRestartRepository.getAllServerRestartDataStream()).thenReturn(Stream.of("SER-1", "SER-2"));

        List<String> actual = serverRestartService.getAllServerRestartDataStream().toList();

        assertEquals(List.of("SER-1", "SER-2"), actual);
    }

    @Test
    void putServerRestartDataDelegatesToRepository() {
        List<String> serverIds = List.of("SER-1", "SER-2");

        serverRestartService.putServerRestartData(serverIds);

        verify(serverRestartRepository).insertServerRestartData(serverIds);
    }
}

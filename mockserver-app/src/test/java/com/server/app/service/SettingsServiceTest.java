package com.server.app.service;

import com.server.app.repository.SettingsRepository;
import com.server.app.support.ReflectionTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {
    @Mock
    private SettingsRepository settingsRepository;

    private SettingsService settingsService;

    @BeforeEach
    void setUp() {
        settingsService = new SettingsService();
        ReflectionTestUtil.setField(settingsService, "settingsRepository", settingsRepository);
    }

    @Test
    void syncConfigDelegatesToRepository() {
        settingsService.syncConfig();

        verify(settingsRepository).syncConfiguration();
    }

    @Test
    void updateConfigDelegatesToRepository() {
        settingsService.updateConfig();

        verify(settingsRepository).updateConfiguration();
    }

    @Test
    void initAndSyncSettingsInitializesTableWhenNoRowsExist() {
        when(settingsRepository.getRowCount()).thenReturn(0);

        settingsService.initAndSyncSettings();

        verify(settingsRepository).initSettingsTable();
        verify(settingsRepository).syncConfiguration();
    }

    @Test
    void initAndSyncSettingsSkipsInitializationWhenRowsExist() {
        when(settingsRepository.getRowCount()).thenReturn(2);

        settingsService.initAndSyncSettings();

        verify(settingsRepository, never()).initSettingsTable();
        verify(settingsRepository).syncConfiguration();
    }
}

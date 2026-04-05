package com.server.app.service;

import javafx.application.HostServices;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AppServiceTest {
    @Test
    void openUrlInBrowserUsesConfiguredHostServices() {
        HostServices hostServices = mock(HostServices.class);
        AppService.INSTANCE.setHostServices(hostServices);

        AppService.INSTANCE.openUrlInBrowser("https://example.com");

        verify(hostServices).showDocument("https://example.com");
    }

    @Test
    void gettersExposeInitializedServices() {
        assertNotNull(AppService.INSTANCE.getSettingsService());
        assertNotNull(AppService.INSTANCE.getServerRestartService());
        assertNotNull(AppService.INSTANCE.getTableDataService());
    }
}

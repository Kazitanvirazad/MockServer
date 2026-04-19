package com.server.app.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppRepositoryTest {
    @Test
    void singletonExposesRepositories() {
        assertNotNull(AppRepository.INSTANCE.getSettingsRepository());
        assertNotNull(AppRepository.INSTANCE.getServerRestartRepository());
    }
}

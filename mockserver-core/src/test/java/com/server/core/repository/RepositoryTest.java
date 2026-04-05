package com.server.core.repository;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class RepositoryTest {
    @Test
    void singletonExposesRepositories() {
        assertNotNull(Repository.INSTANCE.getCollectionRepository());
        assertNotNull(Repository.INSTANCE.getServerRepository());
    }
}

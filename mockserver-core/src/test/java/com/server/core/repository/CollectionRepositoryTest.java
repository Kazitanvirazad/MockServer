package com.server.core.repository;

import com.server.core.config.DBConfig;
import com.server.core.model.data.Collection;
import com.server.core.support.ReflectionTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CollectionRepositoryTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private CollectionRepository collectionRepository;
    private DataSource originalDataSource;

    @BeforeEach
    void setUp() throws Exception {
        collectionRepository = new CollectionRepository();
        originalDataSource = (DataSource) ReflectionTestUtil.getField(DBConfig.INSTANCE, "dataSource");
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", originalDataSource);
    }

    @Test
    void getCollectionByIdMapsResultSetData() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("COL-1");
        when(resultSet.getString(2)).thenReturn("Orders");
        when(resultSet.getTimestamp(3)).thenReturn(Timestamp.valueOf("2026-04-05 09:00:00"));
        when(resultSet.getTimestamp(4)).thenReturn(Timestamp.valueOf("2026-04-05 10:00:00"));

        Optional<Collection> actual = collectionRepository.getCollectionById("COL-1");

        assertTrue(actual.isPresent());
        assertEquals("COL-1", actual.get().getCollectionId());
        assertEquals("Orders", actual.get().getCollectionName());
        verify(preparedStatement).setString(1, "COL-1");
    }

    @Test
    void getCollectionByNameReturnsEmptyWhenDatabaseFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("boom"));

        Optional<Collection> actual = collectionRepository.getCollectionByName("Orders");

        assertTrue(actual.isEmpty());
    }

    @Test
    void getCollectionByNameMapsResultSetData() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("COL-2");
        when(resultSet.getString(2)).thenReturn("Payments");
        when(resultSet.getTimestamp(3)).thenReturn(Timestamp.valueOf("2026-04-05 11:00:00"));
        when(resultSet.getTimestamp(4)).thenReturn(Timestamp.valueOf("2026-04-05 12:00:00"));

        Optional<Collection> actual = collectionRepository.getCollectionByName("Payments");

        assertTrue(actual.isPresent());
        assertEquals("COL-2", actual.get().getCollectionId());
        assertEquals("Payments", actual.get().getCollectionName());
        verify(preparedStatement).setString(1, "Payments");
    }

    @Test
    void getCollectionStreamReturnsAllRows() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("COL-1", "COL-2");
        when(resultSet.getString(2)).thenReturn("Orders", "Payments");
        when(resultSet.getTimestamp(3)).thenReturn(
                Timestamp.valueOf("2026-04-05 09:00:00"),
                Timestamp.valueOf("2026-04-05 09:30:00")
        );
        when(resultSet.getTimestamp(4)).thenReturn(
                Timestamp.valueOf("2026-04-05 10:00:00"),
                Timestamp.valueOf("2026-04-05 10:30:00")
        );

        List<Collection> actual = collectionRepository.getCollectionStream().toList();

        assertEquals(2, actual.size());
        assertEquals("COL-1", actual.get(0).getCollectionId());
        assertEquals("COL-2", actual.get(1).getCollectionId());
    }

    @Test
    void createCollectionStoresProvidedValues() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int status = collectionRepository.createCollection("COL-10", "Orders");

        assertEquals(1, status);
        verify(preparedStatement).setString(1, "COL-10");
        verify(preparedStatement).setString(2, "Orders");
        verify(connection).commit();
    }

    @Test
    void createCollectionFromEntityStoresTimestamps() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(2);
        Collection collection = new Collection("Orders");
        collection.setCreatedOn(Timestamp.valueOf("2026-04-05 08:00:00"));
        collection.setModifiedOn(Timestamp.valueOf("2026-04-05 08:05:00"));

        int status = collectionRepository.createCollection("COL-11", collection);

        assertEquals(2, status);
        verify(preparedStatement).setTimestamp(3, collection.getCreatedOn());
        verify(preparedStatement).setTimestamp(4, collection.getModifiedOn());
    }

    @Test
    void editCollectionReturnsZeroWhenDatabaseFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("boom"));

        int status = collectionRepository.editCollection("COL-12", "Updated");

        assertEquals(0, status);
    }

    @Test
    void deleteCollectionDeletesById() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int status = collectionRepository.deleteCollection("COL-13");

        assertEquals(1, status);
        verify(preparedStatement).setString(1, "COL-13");
    }
}

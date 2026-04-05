package com.server.core.util;

import com.server.core.config.DBConfig;
import com.server.core.function.CheckedConsumer;
import com.server.core.function.CheckedFunction;
import com.server.core.support.ReflectionTestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatabaseUtilTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);

    private DataSource originalDataSource;

    @BeforeEach
    void setUp() throws Exception {
        originalDataSource = (DataSource) ReflectionTestUtil.getField(DBConfig.INSTANCE, "dataSource");
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", originalDataSource);
    }

    @Test
    void executeCreateQueryCommitsSuccessfulUpdates() throws Exception {
        when(preparedStatement.executeUpdate()).thenReturn(3);

        int status = DatabaseUtil.executeCreateQuery(conn -> conn.prepareStatement("INSERT").executeUpdate());

        assertEquals(3, status);
        verify(connection).setAutoCommit(false);
        verify(connection).beginRequest();
        verify(connection).commit();
    }

    @Test
    void executeUpdateQueryRollsBackAndRethrowsOnFailure() throws Exception {
        doThrow(new IllegalStateException("boom")).when(connection).beginRequest();

        assertThrows(IllegalStateException.class, () -> DatabaseUtil.executeUpdateQuery(conn -> 1));
        verify(connection).rollback();
    }

    @Test
    void executeFetchQueryConsumerUsesMockedConnection() throws Exception {
        DatabaseUtil.executeFetchQuery((CheckedConsumer<Connection>) conn -> assertEquals(connection, conn));

        verify(dataSource).getConnection();
    }

    @Test
    void executeFetchQueryFunctionReturnsValue() throws Exception {
        Optional<String> actual = DatabaseUtil.executeFetchQuery(
                (CheckedFunction<Connection, Optional<String>>) conn -> Optional.of("value"));

        assertEquals(Optional.of("value"), actual);
    }

    @Test
    void executeFetchQueryConsumerRethrowsWhenConnectionLookupFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new IllegalStateException("boom"));

        assertThrows(IllegalStateException.class,
                () -> DatabaseUtil.executeFetchQuery((CheckedConsumer<Connection>) conn -> {
                }));
    }

    @Test
    void executeFetchQueryFunctionRethrowsWhenConnectionLookupFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new IllegalStateException("boom"));

        assertThrows(IllegalStateException.class,
                () -> DatabaseUtil.executeFetchQuery((CheckedFunction<Connection, Optional<String>>) conn -> Optional.of("value")));
    }

    @Test
    void readStartupSqlScriptIgnoresMissingAdditionalResource() {
        assertTrue(DatabaseUtil.readStartupSQLScript("/missing-resource.sql").stream().anyMatch(query -> !query.isBlank()));
    }

    @Test
    void readStartupSqlScriptReturnsQueriesFromClasspath() {
        assertTrue(DatabaseUtil.readStartupSQLScript().stream().anyMatch(query -> !query.isBlank()));
    }
}

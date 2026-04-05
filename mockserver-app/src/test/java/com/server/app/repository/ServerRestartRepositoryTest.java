package com.server.app.repository;

import com.server.app.support.ReflectionTestUtil;
import com.server.core.config.DBConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerRestartRepositoryTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private ServerRestartRepository serverRestartRepository;
    private DataSource originalDataSource;

    @BeforeEach
    void setUp() throws Exception {
        serverRestartRepository = new ServerRestartRepository();
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
    void deleteServerRestartDataExecutesDeleteStatement() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        serverRestartRepository.deleteServerRestartData();

        verify(preparedStatement).executeUpdate();
    }

    @Test
    void getAllServerRestartDataStreamReturnsStoredIds() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("SER-1", "SER-2");

        List<String> actual = serverRestartRepository.getAllServerRestartDataStream().toList();

        assertEquals(List.of("SER-1", "SER-2"), actual);
    }

    @Test
    void insertServerRestartDataBuildsParameterizedStatement() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(2);

        serverRestartRepository.insertServerRestartData(List.of("SER-1", "SER-2"));

        verify(preparedStatement).setString(1, "SER-1");
        verify(preparedStatement).setString(2, "SER-2");
    }
}

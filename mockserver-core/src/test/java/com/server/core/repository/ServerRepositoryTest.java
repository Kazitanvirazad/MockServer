package com.server.core.repository;

import com.server.core.config.DBConfig;
import com.server.core.model.data.Cookie;
import com.server.core.model.data.Header;
import com.server.core.model.data.Server;
import com.server.core.support.ReflectionTestUtil;
import com.server.core.util.Serializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerRepositoryTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private ServerRepository serverRepository;
    private DataSource originalDataSource;

    @BeforeEach
    void setUp() throws Exception {
        serverRepository = new ServerRepository();
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
    void getServerByIdMapsDatabaseRow() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("SER-1");
        when(resultSet.getString(2)).thenReturn("Orders");
        when(resultSet.getString(3)).thenReturn("/orders");
        when(resultSet.getInt(4)).thenReturn(201);
        when(resultSet.getString(5)).thenReturn("POST");
        when(resultSet.getLong(6)).thenReturn(250L);
        when(resultSet.getInt(7)).thenReturn(8080);
        when(resultSet.getString(8)).thenReturn("{\"ok\":true}");
        when(resultSet.getString(9)).thenReturn("headers");
        when(resultSet.getString(10)).thenReturn("cookies");
        when(resultSet.getString(11)).thenReturn("COL-1");
        when(resultSet.getTimestamp(12)).thenReturn(Timestamp.valueOf("2026-04-05 09:00:00"));
        when(resultSet.getTimestamp(13)).thenReturn(Timestamp.valueOf("2026-04-05 10:00:00"));
        when(resultSet.getInt(14)).thenReturn(1);
        when(resultSet.getString(15)).thenReturn("C:/tmp/response.bin");

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            serializerMock.when(() -> Serializer.deSerializeList("headers", Header.class))
                    .thenReturn(Optional.of(List.of(new Header("Content-Type", "application/json"))));
            serializerMock.when(() -> Serializer.deSerializeList("cookies", Cookie.class))
                    .thenReturn(Optional.of(List.of(new Cookie("example.com", null, false, null,
                            "session", false, "/", "LAX", false, "abc"))));

            Optional<Server> actual = serverRepository.getServerById("SER-1");

            assertTrue(actual.isPresent());
            assertEquals("SER-1", actual.get().getServerId());
            assertEquals("Orders", actual.get().getServerName());
            assertEquals("/orders", actual.get().getUrlEndpoint());
            assertEquals(1, actual.get().getHeaders().size());
            assertEquals(1, actual.get().getCookies().size());
            assertTrue(actual.get().isDefaultResponseBinary());
        }
    }

    @Test
    void getServerByIdReturnsEmptyWhenNoRowFound() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            Optional<Server> actual = serverRepository.getServerById("SER-X");

            assertTrue(actual.isEmpty());
            serializerMock.verifyNoInteractions();
        }
    }

    @Test
    void getServerByIdHandlesNullResponseAndEmptyOptionalValues() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(1)).thenReturn("SER-2");
        when(resultSet.getString(2)).thenReturn("Health");
        when(resultSet.getString(3)).thenReturn("/health");
        when(resultSet.getInt(4)).thenReturn(200);
        when(resultSet.getString(5)).thenReturn("GET");
        when(resultSet.getLong(6)).thenReturn(0L);
        when(resultSet.getInt(7)).thenReturn(8081);
        when(resultSet.getString(8)).thenReturn((String) null);
        when(resultSet.getString(9)).thenReturn("headers");
        when(resultSet.getString(10)).thenReturn("cookies");
        when(resultSet.getString(11)).thenReturn("COL-2");
        when(resultSet.getTimestamp(12)).thenReturn(Timestamp.valueOf("2026-04-05 09:00:00"));
        when(resultSet.getTimestamp(13)).thenReturn(Timestamp.valueOf("2026-04-05 10:00:00"));
        when(resultSet.getInt(14)).thenReturn(0);
        when(resultSet.getString(15)).thenReturn("");

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            serializerMock.when(() -> Serializer.deSerializeList("headers", Header.class)).thenReturn(Optional.empty());
            serializerMock.when(() -> Serializer.deSerializeList("cookies", Cookie.class)).thenReturn(Optional.empty());

            Optional<Server> actual = serverRepository.getServerById("SER-2");

            assertTrue(actual.isPresent());
            assertEquals("", actual.get().getResponseData());
            assertEquals(null, actual.get().getHeaders());
            assertEquals(null, actual.get().getCookies());
            assertTrue(!actual.get().isDefaultResponseBinary());
        }
    }

    @Test
    void getServersByCollectionStreamReturnsAllRows() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString(1)).thenReturn("SER-1", "SER-2");
        when(resultSet.getString(2)).thenReturn("Orders", "Payments");
        when(resultSet.getString(3)).thenReturn("/orders", "/payments");
        when(resultSet.getInt(4)).thenReturn(200, 204);
        when(resultSet.getString(5)).thenReturn("GET", "DELETE");
        when(resultSet.getLong(6)).thenReturn(0L, 100L);
        when(resultSet.getInt(7)).thenReturn(8080, 9090);
        when(resultSet.getString(8)).thenReturn("{}", "");
        when(resultSet.getString(9)).thenReturn("headers-1", "headers-2");
        when(resultSet.getString(10)).thenReturn("cookies-1", "cookies-2");
        when(resultSet.getString(11)).thenReturn("COL-1", "COL-1");
        when(resultSet.getTimestamp(12)).thenReturn(
                Timestamp.valueOf("2026-04-05 09:00:00"),
                Timestamp.valueOf("2026-04-05 09:05:00")
        );
        when(resultSet.getTimestamp(13)).thenReturn(
                Timestamp.valueOf("2026-04-05 10:00:00"),
                Timestamp.valueOf("2026-04-05 10:05:00")
        );
        when(resultSet.getInt(14)).thenReturn(0, 0);
        when(resultSet.getString(15)).thenReturn("", "");

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            serializerMock.when(() -> Serializer.deSerializeList(anyString(), eq(Header.class))).thenReturn(Optional.empty());
            serializerMock.when(() -> Serializer.deSerializeList(anyString(), eq(Cookie.class))).thenReturn(Optional.empty());

            List<Server> actual = serverRepository.getServersByCollectionStream("COL-1").toList();

            assertEquals(2, actual.size());
            assertEquals("SER-1", actual.get(0).getServerId());
            assertEquals("SER-2", actual.get(1).getServerId());
        }
    }

    @Test
    void createServerStoresProvidedFields() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Server server = server("SER-10");

        int status = serverRepository.createServer(server, "[]", "[]");

        assertEquals(1, status);
        verify(preparedStatement).setString(1, "SER-10");
        verify(preparedStatement).setString(11, "COL-1");
    }

    @Test
    void createServerStoresBinaryFlagWhenEnabled() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Server server = server("SER-10B");
        server.setDefaultResponseBinary(true);
        server.setResponseBinaryPath("C:/response.bin");

        int status = serverRepository.createServer(server, "[]", "[]");

        assertEquals(1, status);
        verify(preparedStatement).setInt(12, 1);
        verify(preparedStatement).setString(13, "C:/response.bin");
    }

    @Test
    void createImportedServerStoresProvidedTimestamps() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Server server = server("SER-11");
        server.setCreatedOn(Timestamp.valueOf("2026-04-05 08:00:00"));
        server.setModifiedOn(Timestamp.valueOf("2026-04-05 08:05:00"));

        int status = serverRepository.createServer(server, "[]", "[]", "COL-99", "SER-99");

        assertEquals(1, status);
        verify(preparedStatement).setString(1, "SER-99");
        verify(preparedStatement).setTimestamp(11, server.getCreatedOn());
        verify(preparedStatement).setTimestamp(12, server.getModifiedOn());
        verify(preparedStatement).setString(13, "COL-99");
    }

    @Test
    void updateServerReturnsZeroWhenDatabaseFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new RuntimeException("boom"));

        int status = serverRepository.updateServer(server("SER-12"), "[]", "[]");

        assertEquals(0, status);
    }

    @Test
    void updateServerStoresProvidedFieldsOnSuccess() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        Server server = server("SER-14");
        server.setDefaultResponseBinary(true);
        server.setResponseBinaryPath("C:/updated.bin");

        int status = serverRepository.updateServer(server, "[1]", "[2]");

        assertEquals(1, status);
        verify(preparedStatement).setString(8, "[1]");
        verify(preparedStatement).setString(9, "[2]");
        verify(preparedStatement).setInt(11, 1);
        verify(preparedStatement).setString(12, "C:/updated.bin");
        verify(preparedStatement).setString(13, "SER-14");
    }

    @Test
    void deleteServerDeletesById() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        int status = serverRepository.deleteServer("SER-13");

        assertEquals(1, status);
        verify(preparedStatement).setString(1, "SER-13");
    }

    private static Server server(String serverId) {
        Server server = new Server();
        server.setServerId(serverId);
        server.setServerName("Orders");
        server.setUrlEndpoint("/orders");
        server.setResponseCode(200);
        server.setDelay(100L);
        server.setPort(8080);
        server.setResponseData("{}");
        server.setCollectionId("COL-1");
        server.setDefaultResponseBinary(false);
        server.setResponseBinaryPath("");
        return server;
    }
}

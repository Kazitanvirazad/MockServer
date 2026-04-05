package com.server.app.repository;

import com.server.app.config.AppConfig;
import com.server.app.model.data.Configuration;
import com.server.app.support.ReflectionTestUtil;
import com.server.core.config.DBConfig;
import com.server.core.util.Serializer;
import javafx.scene.image.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SettingsRepositoryTest {
    private final DataSource dataSource = mock(DataSource.class);
    private final Connection connection = mock(Connection.class);
    private final PreparedStatement preparedStatement = mock(PreparedStatement.class);
    private final ResultSet resultSet = mock(ResultSet.class);

    private SettingsRepository settingsRepository;
    private DataSource originalDataSource;
    private boolean originalStartServerOnStartup;
    private MockedConstruction<Image> imageConstruction;

    @BeforeEach
    void setUp() throws Exception {
        settingsRepository = new SettingsRepository();
        originalDataSource = (DataSource) ReflectionTestUtil.getField(DBConfig.INSTANCE, "dataSource");
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", dataSource);
        imageConstruction = org.mockito.Mockito.mockConstruction(Image.class);
        originalStartServerOnStartup = AppConfig.INSTANCE.getConfiguration().isStartServerOnStartup();
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    @AfterEach
    void tearDown() {
        ReflectionTestUtil.setField(DBConfig.INSTANCE, "dataSource", originalDataSource);
        AppConfig.INSTANCE.getConfiguration().setStartServerOnStartup(originalStartServerOnStartup);
        imageConstruction.close();
    }

    @Test
    void syncConfigurationUpdatesAppConfigFromSerializedData() throws Exception {
        Configuration configuration = new Configuration();
        configuration.setStartServerOnStartup(true);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getString(2)).thenReturn("{\"startServerOnStartup\":true}");

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            serializerMock.when(() -> Serializer.deSerialize("{\"startServerOnStartup\":true}", Configuration.class))
                    .thenReturn(Optional.of(configuration));

            settingsRepository.syncConfiguration();

            assertTrue(AppConfig.INSTANCE.getConfiguration().isStartServerOnStartup());
        }
    }

    @Test
    void updateConfigurationPersistsSerializedConfig() throws Exception {
        AppConfig.INSTANCE.getConfiguration().setStartServerOnStartup(true);
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        try (MockedStatic<Serializer> serializerMock = mockStatic(Serializer.class)) {
            serializerMock.when(() -> Serializer.serialize(AppConfig.INSTANCE.getConfiguration()))
                    .thenReturn(Optional.of("{\"startServerOnStartup\":true}"));

            settingsRepository.updateConfiguration();

            verify(preparedStatement).setString(1, "{\"startServerOnStartup\":true}");
            verify(connection).commit();
        }
    }

    @Test
    void getRowCountReturnsCountFromDatabase() throws Exception {
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt(1)).thenReturn(4);

        int actual = settingsRepository.getRowCount();

        org.junit.jupiter.api.Assertions.assertEquals(4, actual);
    }

    @Test
    void initSettingsTableInsertsDefaultConfiguration() throws Exception {
        when(preparedStatement.execute()).thenReturn(true);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        settingsRepository.initSettingsTable();

        verify(preparedStatement).setString(1, "{\"startServerOnStartup\":false}");
    }
}

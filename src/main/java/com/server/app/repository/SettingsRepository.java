package com.server.app.repository;

import com.server.app.config.AppConfig;
import com.server.app.model.data.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.server.app.util.DatabaseUtil.executeFetchQuery;
import static com.server.app.util.DatabaseUtil.executeUpdateQuery;
import static com.server.app.util.Serializer.deSerialize;
import static com.server.app.util.Serializer.serialize;

public class SettingsRepository {
    private static final Logger log = LogManager.getLogger(SettingsRepository.class);

    public void syncConfiguration() {
        final String query = """
                SELECT s.id, s.config_json_text FROM settings s WHERE s.id = 1""";
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String configJsonText = resultSet.getString(2);
                    Optional<Configuration> currentConfigOptional = deSerialize(configJsonText, Configuration.class);
                    currentConfigOptional.ifPresent(AppConfig.INSTANCE.getConfiguration()::updateConfiguration);
                }
                resultSet.close();
            });
        } catch (SQLException exception) {
            log.error(exception.getMessage());
        }
    }

    public void updateConfiguration() {
        final String query = """
                UPDATE settings SET config_json_text = ? WHERE id = 1""";
        try {
            Configuration configuration = AppConfig.INSTANCE.getConfiguration();
            Optional<String> configurationJsonTextOptional = serialize(configuration);
            if (configurationJsonTextOptional.isPresent()) {
                executeUpdateQuery(connection -> {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, configurationJsonTextOptional.get());
                    return preparedStatement.executeUpdate();
                });
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    public int getRowCount() {
        final String query = """
                SELECT COUNT(*) FROM settings s""";
        final AtomicInteger count = new AtomicInteger(0);
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    count.set(resultSet.getInt(1));
                }
                resultSet.close();
            });
        } catch (SQLException exception) {
            log.error(exception.getMessage());
        }
        return count.get();
    }

    public void initSettingsTable() {
        final String initConfigJson = """
                {"startServerOnStartup":false}""";
        final String query = """
                INSERT INTO settings (id, config_json_text) VALUES (1, ?)""";
        try {
            executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, initConfigJson);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}

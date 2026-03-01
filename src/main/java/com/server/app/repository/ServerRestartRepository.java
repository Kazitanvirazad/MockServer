package com.server.app.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.server.app.constants.ApplicationConstants.COMMA;
import static com.server.app.constants.ApplicationConstants.SQL_QUERY;
import static com.server.app.util.DatabaseUtil.executeCreateQuery;
import static com.server.app.util.DatabaseUtil.executeFetchQuery;
import static com.server.app.util.DatabaseUtil.executeUpdateQuery;

/**
 * @author Kazi Tanvir Azad
 * @apiNote This class has database repository methods for performing all SQL queries
 * <br>for {@link com.server.app.model.data.Server} restart settings during application startup
 */
public class ServerRestartRepository {
    private static final Logger log = LogManager.getLogger(ServerRestartRepository.class);

    public void deleteServerRestartData() {
        final String query = """
                DELETE FROM server_restart""";
        try {
            executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    public Stream<String> getAllServerRestartDataStream() {
        return getAllServerRestartData().stream();
    }

    private List<String> getAllServerRestartData() {
        final String query = """
                SELECT * FROM server_restart sr""";
        final List<String> serverRestartData = new ArrayList<>();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        serverRestartData.add(resultSet.getString(1));
                    }
                }
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return serverRestartData;
    }

    public void insertServerRestartData(List<String> serverIds) {
        final String query = constructInsertServerRestartDataQuery(serverIds.size());
        try {
            executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                for (int i = 0; i < serverIds.size(); i++) {
                    String serverId = serverIds.get(i);
                    preparedStatement.setString(i + 1, serverId);
                }
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }

    private String constructInsertServerRestartDataQuery(int params) {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO server_restart (server_id) VALUES ");
        for (int i = 0; i < params; i++) {
            stringBuilder.append(SQL_QUERY);
            if (i < params - 1) {
                stringBuilder.append(COMMA);
            }
        }
        return stringBuilder.toString();
    }
}

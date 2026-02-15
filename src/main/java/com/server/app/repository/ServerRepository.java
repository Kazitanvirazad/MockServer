package com.server.app.repository;

import com.server.app.model.data.Cookie;
import com.server.app.model.data.Header;
import com.server.app.model.data.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.server.app.util.DatabaseUtil.executeCreateQuery;
import static com.server.app.util.DatabaseUtil.executeFetchQuery;
import static com.server.app.util.DatabaseUtil.executeUpdateQuery;
import static com.server.app.util.Serializer.deSerializeList;

public class ServerRepository {
    private static final Logger log = LogManager.getLogger(ServerRepository.class);

    public Server getServerById(String serverId) {
        final String query = """
                SELECT s.server_id, s.server_name, s.url_endpoint, s.response_code, s.method, s.delay, s.port,
                s.response_data, s.headers, s.cookies, s.collection_id, s.createdOn, s.modifiedOn
                FROM server s WHERE s.server_id = ?""";
        final Server server = new Server();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, serverId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    server.setServerId(resultSet.getString(1));
                    server.setServerName(resultSet.getString(2));
                    server.setUrlEndpoint(resultSet.getString(3));
                    server.setResponseCode(resultSet.getInt(4));
                    server.setMethod(resultSet.getString(5));
                    server.setDelay(resultSet.getLong(6));
                    server.setPort(resultSet.getInt(7));
                    if (null != resultSet.getString(8)) {
                        server.setResponseData(resultSet.getString(8));
                    }
                    Optional<List<Header>> headers = deSerializeList(resultSet.getString(9), Header.class);
                    Optional<List<Cookie>> cookies = deSerializeList(resultSet.getString(10), Cookie.class);
                    headers.ifPresent(server::setHeaders);
                    cookies.ifPresent(server::setCookies);

                    server.setCollectionId(resultSet.getString(11));
                    server.setCreatedOn(resultSet.getTimestamp(12));
                    server.setModifiedOn(resultSet.getTimestamp(13));
                }
                resultSet.close();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return server;
    }

    public Stream<Server> getServersByCollectionStream(String collectionId) {
        return getServersByCollection(collectionId).stream();
    }

    private List<Server> getServersByCollection(String collectionId) {
        final String query = """
                SELECT s.server_id, s.server_name, s.url_endpoint, s.response_code, s."method", s.delay, s.port,
                s.response_data, s.headers, s.cookies, s.collection_id, s.createdOn, s.modifiedOn
                 FROM server s WHERE s.collection_id = ?""";
        final List<Server> servers = new ArrayList<>();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionId);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Server server = new Server();
                    server.setServerId(resultSet.getString(1));
                    server.setServerName(resultSet.getString(2));
                    server.setUrlEndpoint(resultSet.getString(3));
                    server.setResponseCode(resultSet.getInt(4));
                    server.setMethod(resultSet.getString(5));
                    server.setDelay(resultSet.getLong(6));
                    server.setPort(resultSet.getInt(7));
                    if (null != resultSet.getString(8)) {
                        server.setResponseData(resultSet.getString(8));
                    }

                    Optional<List<Header>> headers = deSerializeList(resultSet.getString(9), Header.class);
                    Optional<List<Cookie>> cookies = deSerializeList(resultSet.getString(10), Cookie.class);
                    headers.ifPresent(server::setHeaders);
                    cookies.ifPresent(server::setCookies);

                    server.setCollectionId(resultSet.getString(11));
                    server.setCreatedOn(resultSet.getTimestamp(12));
                    server.setModifiedOn(resultSet.getTimestamp(13));

                    servers.add(server);
                }
                resultSet.close();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return servers;
    }

    public int createServer(Server server, String headerJson, String cookieJson) {
        int status = 0;
        final String query = """
                INSERT INTO server (server_id,server_name,url_endpoint,response_code,method,delay,port,response_data,
                headers,cookies,collection_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)""";
        try {
            status = executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, server.getServerId());
                preparedStatement.setString(2, server.getServerName());
                preparedStatement.setString(3, server.getUrlEndpoint());
                preparedStatement.setInt(4, server.getResponseCode());
                preparedStatement.setString(5, server.getMethod().name());
                preparedStatement.setLong(6, server.getDelay());
                preparedStatement.setInt(7, server.getPort());
                preparedStatement.setString(8, server.getResponseData());
                preparedStatement.setString(9, headerJson);
                preparedStatement.setString(10, cookieJson);
                preparedStatement.setString(11, server.getCollectionId());
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int createServer(Server server, String headerJson, String cookieJson, String collectionId, String serverId) {
        int status = 0;
        final String query = """
                INSERT INTO server (server_id,server_name,url_endpoint,response_code,method,delay,port,response_data,
                headers,cookies,createdOn,modifiedOn,collection_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)""";
        try {
            status = executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, serverId);
                preparedStatement.setString(2, server.getServerName());
                preparedStatement.setString(3, server.getUrlEndpoint());
                preparedStatement.setInt(4, server.getResponseCode());
                preparedStatement.setString(5, server.getMethod().name());
                preparedStatement.setLong(6, server.getDelay());
                preparedStatement.setInt(7, server.getPort());
                preparedStatement.setString(8, server.getResponseData());
                preparedStatement.setString(9, headerJson);
                preparedStatement.setString(10, cookieJson);
                preparedStatement.setTimestamp(11, server.getCreatedOn());
                preparedStatement.setTimestamp(12, server.getModifiedOn());
                preparedStatement.setString(13, collectionId);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int updateServer(Server server, String headerJson, String cookieJson) {
        int status = 0;
        final String query = """
                UPDATE server SET server_name = ?, url_endpoint = ?, response_code = ?, method = ?, delay = ?, port = ?,
                response_data = ?, headers = ?, cookies = ?, collection_id = ?, modifiedOn = datetime('now','localtime')
                 WHERE server_id = ?""";
        try {
            status = executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, server.getServerName());
                preparedStatement.setString(2, server.getUrlEndpoint());
                preparedStatement.setInt(3, server.getResponseCode());
                preparedStatement.setString(4, server.getMethod().name());
                preparedStatement.setLong(5, server.getDelay());
                preparedStatement.setInt(6, server.getPort());
                preparedStatement.setString(7, server.getResponseData());
                preparedStatement.setString(8, headerJson);
                preparedStatement.setString(9, cookieJson);
                preparedStatement.setString(10, server.getCollectionId());
                preparedStatement.setString(11, server.getServerId());
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int deleteServer(String serverId) {
        int status = 0;
        final String query = """
                DELETE FROM server WHERE server_id=?""";
        try {
            status = executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, serverId);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }
}

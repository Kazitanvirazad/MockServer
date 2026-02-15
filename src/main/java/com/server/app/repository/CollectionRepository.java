package com.server.app.repository;

import com.server.app.model.data.Collection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.server.app.util.DatabaseUtil.executeCreateQuery;
import static com.server.app.util.DatabaseUtil.executeFetchQuery;
import static com.server.app.util.DatabaseUtil.executeUpdateQuery;

public class CollectionRepository {
    private static final Logger log = LogManager.getLogger(CollectionRepository.class);

    public Collection getCollectionById(String collectionId) {
        final String query = """
                SELECT c.collection_id, c.collection_name, c.createdOn, c.modifiedOn FROM collection c
                WHERE c.collection_id = ?""";
        final Collection collection = new Collection();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    collection.setCollectionId(resultSet.getString(1));
                    collection.setCollectionName(resultSet.getString(2));
                    collection.setCreatedOn(resultSet.getTimestamp(3));
                    collection.setModifiedOn(resultSet.getTimestamp(4));
                }
                resultSet.close();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return collection;
    }

    public Collection getCollectionByName(String collectionName) {
        final String query = """
                SELECT c.collection_id, c.collection_name, c.createdOn, c.modifiedOn FROM collection c
                WHERE c.collection_name = ?""";
        final Collection collection = new Collection();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    collection.setCollectionId(resultSet.getString(1));
                    collection.setCollectionName(resultSet.getString(2));
                    collection.setCreatedOn(resultSet.getTimestamp(3));
                    collection.setModifiedOn(resultSet.getTimestamp(4));
                }
                resultSet.close();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return collection;
    }

    private List<Collection> getCollections() {
        final String query = """
                SELECT c.collection_id, c.collection_name, c.createdOn, c.modifiedOn FROM collection c""";
        final List<Collection> collections = new ArrayList<>();
        try {
            executeFetchQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    Collection collection = new Collection();
                    collection.setCollectionId(resultSet.getString(1));
                    collection.setCollectionName(resultSet.getString(2));
                    collection.setCreatedOn(resultSet.getTimestamp(3));
                    collection.setModifiedOn(resultSet.getTimestamp(4));
                    collections.add(collection);
                }
                resultSet.close();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return collections;
    }

    public Stream<Collection> getCollectionStream() {
        return getCollections().stream();
    }

    public int createCollection(final String collectionId, final String collectionName) {
        int status = 0;
        final String query = """
                INSERT INTO collection (collection_id, collection_name)
                values (?,?)""";
        try {
            status = executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionId);
                preparedStatement.setString(2, collectionName);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int createCollection(final String collectionId, final Collection collection) {
        int status = 0;
        final String query = """
                INSERT INTO collection (collection_id, collection_name, createdOn, modifiedOn)
                values (?,?,?,?)""";
        try {
            status = executeCreateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionId);
                preparedStatement.setString(2, collection.getCollectionName());
                preparedStatement.setTimestamp(3, collection.getCreatedOn());
                preparedStatement.setTimestamp(4, collection.getModifiedOn());
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int editCollection(final String collectionId, final String collectionName) {
        int status = 0;
        final String query = """
                UPDATE collection SET collection_name = ?, modifiedOn = datetime('now','localtime')
                WHERE collection_id = ?""";
        try {
            status = executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionName);
                preparedStatement.setString(2, collectionId);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }

    public int deleteCollection(String collectionId) {
        int status = 0;
        final String query = """
                DELETE FROM collection WHERE collection_id=?""";
        try {
            status = executeUpdateQuery(connection -> {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, collectionId);
                return preparedStatement.executeUpdate();
            });
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return status;
    }
}

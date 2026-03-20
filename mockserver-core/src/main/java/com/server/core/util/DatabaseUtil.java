package com.server.core.util;

import com.server.core.config.DBConfig;
import com.server.core.function.CheckedConsumer;
import com.server.core.function.CheckedFunction;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.server.core.constants.CommonConstants.BLOCK_COMMENT_END;
import static com.server.core.constants.CommonConstants.BLOCK_COMMENT_START;
import static com.server.core.constants.CommonConstants.SQL_COMMENT;
import static com.server.core.constants.CommonConstants.SQL_DDL_QUERY_FILE_PATH;
import static com.server.core.constants.CommonConstants.SQL_PRAGMA_ENABLE_FOREIGN_KEY_QUERY;
import static com.server.core.constants.CommonConstants.SQL_QUERY_SEPARATOR;

/**
 * @author Kazi Tanvir Azad
 */
public final class DatabaseUtil {
    private static final Logger log = LogManager.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    /**
     * Executes DML or DDL query in the consumer and returns the status
     *
     * @param updateFunction {@link CheckedFunction}<{@link Connection}, {@link Integer}>
     * @return {@link Integer} Status of the result returned by the {@link CheckedFunction}
     * @throws Exception if a database access error occurs
     */
    public static Integer executeUpdateQuery(CheckedFunction<Connection, Integer> updateFunction) throws Exception {
        return execute(updateFunction);
    }

    /**
     * Executes DML or DDL query in the consumer and returns the status
     *
     * @param createFunction {@link CheckedFunction}<{@link Connection}, {@link Integer}>
     * @return {@link Integer} Status of the result returned by the {@link CheckedFunction}
     * @throws Exception if a database access error occurs
     */
    public static Integer executeCreateQuery(CheckedFunction<Connection, Integer> createFunction) throws Exception {
        return execute(createFunction);
    }

    /**
     * Executes DML or DDL query in the consumer and returns the status
     *
     * @param function {@link CheckedFunction}<{@link Connection}, {@link Integer}>
     * @return {@link Integer} Status of the result returned by the {@link CheckedFunction}
     * @throws Exception if a database access error occurs
     */
    private static Integer execute(CheckedFunction<Connection, Integer> function) throws Exception {
        final DataSource dataSource = DBConfig.INSTANCE.getDataSource();
        int status;
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        try (connection) {
            connection.beginRequest();
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_PRAGMA_ENABLE_FOREIGN_KEY_QUERY);
            preparedStatement.execute();
            status = function.apply(connection);
            connection.commit();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            if (!connection.isClosed()) {
                connection.rollback();
            }
            throw exception;
        }
        return status;
    }

    /**
     * Executes SQL DQL query in the consumer
     *
     * @param consumer {@link CheckedConsumer}<{@link Connection}> consumer to run with the {@link Connection}
     * @throws Exception if a database access error occurs
     */
    public static void executeFetchQuery(CheckedConsumer<Connection> consumer) throws Exception {
        final DataSource dataSource = DBConfig.INSTANCE.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Executes SQL SELECT query in the function and returns the result of the function
     *
     * @param function {@link CheckedFunction}<{@link Connection}, {@link Optional}<{@code T}>>
     * @param <T>      Return type of the function
     * @return the {@link Optional}<{@code T}> of the result returned by the {@link CheckedFunction}
     * @throws Exception if a database access error occurs
     */
    public static <T> Optional<T> executeFetchQuery(CheckedFunction<Connection, Optional<T>> function) throws Exception {
        final DataSource dataSource = DBConfig.INSTANCE.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            return function.apply(connection);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

    /**
     * Reads and returns the list of all the sql queries from the ddl.sql file from the classpath resource
     *
     * @return {@link List}<{@link String}> of sql queries from the classpath resource file
     */
    public static List<String> readStartupSQLScript() {
        List<String> queries = new ArrayList<>();
        InputStream inputStream = DatabaseUtil.class.getResourceAsStream(SQL_DDL_QUERY_FILE_PATH);
        if (null == inputStream) {
            return queries;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            reader.lines()
                    .filter(StringUtils::isNotBlank)
                    .filter(line -> !line.startsWith(SQL_COMMENT)
                            && !line.startsWith(BLOCK_COMMENT_START)
                            && !line.endsWith(BLOCK_COMMENT_END))
                    .forEach(stringBuilder::append);
            String rawQuery = stringBuilder.toString();
            if (StringUtils.isNotBlank(rawQuery)) {
                queries.addAll(Arrays.asList(rawQuery.split(SQL_QUERY_SEPARATOR)));
            }
        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
        return queries;
    }
}

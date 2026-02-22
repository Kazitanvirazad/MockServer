package com.server.app.util;

import com.server.app.config.DBConfig;
import com.server.app.function.CheckedConsumer;
import com.server.app.function.CheckedFunction;
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

import static com.server.app.constants.ApplicationConstants.BLOCK_COMMENT_END;
import static com.server.app.constants.ApplicationConstants.BLOCK_COMMENT_START;
import static com.server.app.constants.ApplicationConstants.SQL_QUERY_SEPARATOR;
import static com.server.app.constants.ApplicationConstants.SQL_COMMENT;
import static com.server.app.constants.ApplicationConstants.SQL_DDL_QUERY_FILE_PATH;
import static com.server.app.constants.ApplicationConstants.SQL_PRAGMA_ENABLE_FOREIGN_KEY_QUERY;

/**
 * author: Kazi Tanvir Azad
 */
public final class DatabaseUtil {
    private static final Logger log = LogManager.getLogger(DatabaseUtil.class);

    private DatabaseUtil() {
        throw new AssertionError("Initialization of this class is not allowed");
    }

    public static Integer executeUpdateQuery(CheckedFunction<Connection, Integer> updateFunction) throws Exception {
        return execute(updateFunction);
    }

    public static Integer executeCreateQuery(CheckedFunction<Connection, Integer> createFunction) throws Exception {
        return execute(createFunction);
    }

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

    public static void executeFetchQuery(CheckedConsumer<Connection> consumer) throws Exception {
        final DataSource dataSource = DBConfig.INSTANCE.getDataSource();
        try (Connection connection = dataSource.getConnection()) {
            consumer.accept(connection);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw exception;
        }
    }

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

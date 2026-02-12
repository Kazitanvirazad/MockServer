package com.server.app.config;

import com.mchange.v2.c3p0.DataSources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public enum DBConfig {
    INSTANCE;
    private DataSource dataSource;
    private final Logger log = LogManager.getLogger(DBConfig.class);

    public DataSource getDataSource() {
        return dataSource;
    }

    {
        final String DB_URL = "jdbc:sqlite:mockserver.db";
        SQLiteConfig config = new SQLiteConfig();
        config.setReadOnly(false);
        config.setPageSize(4096);
        config.setCacheSize(2000);
        config.setSynchronous(SQLiteConfig.SynchronousMode.FULL);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);

        SQLiteDataSource dataSourceUnPooled = new SQLiteDataSource(config);
        dataSourceUnPooled.setUrl(DB_URL);
        try {
            this.dataSource = DataSources.pooledDataSource(dataSourceUnPooled);
        } catch (SQLException exception) {
            log.error("Failed to initialize the Database");
            log.error(exception.getMessage());
        }
    }
}

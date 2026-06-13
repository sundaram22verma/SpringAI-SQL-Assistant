package madhav.SpringAI.service.impl;

import madhav.SpringAI.model.DatabaseConnection;
import madhav.SpringAI.service.DataSourceManager;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DataSourceManagerImpl implements DataSourceManager {

    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private DatabaseConnection currentConnection;

    public DataSourceManagerImpl(DataSource defaultDataSource) {
        // Initialize with default datasource from application.properties
        this.dataSource = defaultDataSource;
        this.jdbcTemplate = new JdbcTemplate(defaultDataSource);
    }

    @Override
    public void connect(DatabaseConnection connection) {
        this.dataSource = DataSourceBuilder.create()
                .url(connection.getUrl())
                .username(connection.getUsername())
                .password(connection.getPassword())
                .driverClassName(connection.getDriverClassName())
                .build();
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);
        this.currentConnection = connection;
    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    @Override
    public DatabaseConnection getCurrentConnection() {
        return currentConnection;
    }

    @Override
    public boolean isConnected() {
        return dataSource != null;
    }
}

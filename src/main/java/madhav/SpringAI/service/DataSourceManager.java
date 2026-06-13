package madhav.SpringAI.service;

import madhav.SpringAI.model.DatabaseConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;

public interface DataSourceManager {
    void connect(DatabaseConnection connection);
    JdbcTemplate getJdbcTemplate();
    DatabaseConnection getCurrentConnection();
    boolean isConnected();
}

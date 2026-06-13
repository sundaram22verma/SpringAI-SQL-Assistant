package madhav.SpringAI.service.impl;

import madhav.SpringAI.exception.SqlExecutionException;
import madhav.SpringAI.service.SqlExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SqlExecutorServiceImpl implements SqlExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(SqlExecutorServiceImpl.class);
    private final madhav.SpringAI.service.DataSourceManager dataSourceManager;

    public SqlExecutorServiceImpl(madhav.SpringAI.service.DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public List<List<String>> execute(String sql) {
        logger.info("Executing SQL query: {}", sql);
        List<List<String>> result = new ArrayList<>();

        if (!isSafeQuery(sql)) {
            logger.warn("Unsafe SQL detected: {}", sql);
            throw new SqlExecutionException("Unsafe SQL request detected", sql);
        }

        try {
            List<Map<String, Object>> rows = dataSourceManager.getJdbcTemplate().queryForList(sql);
            logger.debug("Query returned {} rows", rows.size());

            if (rows.isEmpty()) {
                return result;
            }

            List<String> headers = new ArrayList<>(rows.get(0).keySet());
            result.add(headers);

            for (Map<String, Object> row : rows) {
                List<String> rowData = new ArrayList<>();
                for (String col : headers) {
                    Object value = row.get(col);
                    rowData.add(value != null ? String.valueOf(value) : "null");
                }
                result.add(rowData);
            }

            return result;
        } catch (Exception e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new SqlExecutionException("Error executing SQL query: " + e.getMessage(), sql, e);
        }
    }

    private boolean isSafeQuery(String sql) {
        String normalizedSql = sql.trim().toUpperCase();
        return normalizedSql.startsWith("SELECT") &&
                !normalizedSql.contains("INSERT") &&
                !normalizedSql.contains("UPSERT") &&
                !normalizedSql.contains("UPDATE") &&
                !normalizedSql.contains("DELETE") &&
                !normalizedSql.contains("DROP") &&
                !normalizedSql.contains("ALTER") &&
                !normalizedSql.contains("TRUNCATE") &&
                !normalizedSql.contains("CREATE") &&
                !normalizedSql.contains("EXEC") &&
                !normalizedSql.contains("EXECUTE");
    }
}

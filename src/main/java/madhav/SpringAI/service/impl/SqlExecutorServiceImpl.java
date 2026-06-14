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

        try {
            JdbcTemplate jdbcTemplate = dataSourceManager.getJdbcTemplate();
            
            String normalizedSql = sql.trim().toUpperCase();
            if (normalizedSql.startsWith("SELECT") || normalizedSql.startsWith("SHOW") || normalizedSql.startsWith("DESCRIBE")) {
                List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
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
            } else {
                // Execute update/delete/insert
                int affectedRows = jdbcTemplate.update(sql);
                List<String> header = List.of("Status");
                List<String> row = List.of("Successfully executed. Affected rows: " + affectedRows);
                result.add(header);
                result.add(row);
            }

            return result;
        } catch (Exception e) {
            logger.error("Error executing SQL query: {}", e.getMessage(), e);
            throw new SqlExecutionException("Error executing SQL query: " + e.getMessage(), sql, e);
        }
    }

    // Removed isSafeQuery as safety is managed by SqlQueryService and TextToSqlService
}

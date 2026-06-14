package madhav.SpringAI;

import madhav.SpringAI.exception.SqlExecutionException;
import madhav.SpringAI.service.DataSourceManager;
import madhav.SpringAI.service.impl.SqlExecutorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlExecutorServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private DataSourceManager dataSourceManager;

    @InjectMocks
    private SqlExecutorServiceImpl sqlExecutorService;

    private List<Map<String, Object>> mockQueryResult;

    @BeforeEach
    void setUp() {
        lenient().when(dataSourceManager.getJdbcTemplate()).thenReturn(jdbcTemplate);
        
        mockQueryResult = new ArrayList<>();
        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("id", 1);
        row1.put("name", "GPT-4");
        row1.put("provider", "OpenAI");
        row1.put("available", true);

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("id", 2);
        row2.put("name", "Claude 3");
        row2.put("provider", "Anthropic");
        row2.put("available", true);

        mockQueryResult.add(row1);
        mockQueryResult.add(row2);
    }

    @Test
    void shouldReturnCorrectResultFormat() {
        String sql = "SELECT id, name, provider, available FROM ai_services";
        when(jdbcTemplate.queryForList(sql)).thenReturn(mockQueryResult);

        List<List<String>> result = sqlExecutorService.execute(sql);

        assertEquals(3, result.size());
        assertEquals(Arrays.asList("id", "name", "provider", "available"), result.get(0));
        assertEquals(Arrays.asList("1", "GPT-4", "OpenAI", "true"), result.get(1));
        assertEquals(Arrays.asList("2", "Claude 3", "Anthropic", "true"), result.get(2));
    }

    @Test
    void shouldReturnEmptyResultForEmptyQueryResult() {
        String sql = "SELECT * FROM ai_services WHERE id = 999";
        when(jdbcTemplate.queryForList(sql)).thenReturn(Collections.emptyList());
        List<List<String>> result = sqlExecutorService.execute(sql);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleNullValuesInQueryResult() {
        String sql = "SELECT id, name, provider, description FROM ai_services";
        List<Map<String, Object>> resultWithNull = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", 1);
        row.put("name", "GPT-4");
        row.put("provider", "OpenAI");
        row.put("description", null);
        resultWithNull.add(row);

        when(jdbcTemplate.queryForList(sql)).thenReturn(resultWithNull);
        List<List<String>> result = sqlExecutorService.execute(sql);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("1", "GPT-4", "OpenAI", "null"), result.get(1));
    }

    @Test
    void shouldExecuteUpdateQuery() {
        String sql = "UPDATE ai_services SET available = false WHERE id = 1";
        when(jdbcTemplate.update(sql)).thenReturn(1);

        List<List<String>> result = sqlExecutorService.execute(sql);

        assertEquals(2, result.size());
        assertEquals(List.of("Status"), result.get(0));
        assertEquals(List.of("Successfully executed. Affected rows: 1"), result.get(1));
    }

    @Test
    void shouldThrowExceptionWhenDatabaseErrorOccurs() {
        String sql = "SELECT * FROM ai_services";
        String errorMessage = "Database connection failed";
        when(jdbcTemplate.queryForList(sql)).thenThrow(new RuntimeException(errorMessage));
        SqlExecutionException exception = assertThrows(
                SqlExecutionException.class,
                () -> sqlExecutorService.execute(sql)
        );

        assertTrue(exception.getMessage().contains("Error executing SQL query"));
        assertEquals(sql, exception.getSql());
    }

    @Test
    void shouldReturnCorrectColumnsInCorrectOrder() {
        String sql = "SELECT name, provider FROM ai_services";
        List<Map<String, Object>> customResult = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("name", "GPT-4");
        row.put("provider", "OpenAI");
        customResult.add(row);

        when(jdbcTemplate.queryForList(sql)).thenReturn(customResult);
        List<List<String>> result = sqlExecutorService.execute(sql);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList("name", "provider"), result.get(0));
        assertEquals(Arrays.asList("GPT-4", "OpenAI"), result.get(1));
    }

    @Test
    void shouldAllowSafeSelectQueries() {
        String[] safeQueries = {
                "SELECT * FROM ai_services",
                "SELECT COUNT(*) FROM ai_services",
                "SELECT name, provider FROM ai_services WHERE available = true",
                "SELECT provider, COUNT(*) FROM ai_services GROUP BY provider"
        };

        for (String sql : safeQueries) {
            when(jdbcTemplate.queryForList(sql)).thenReturn(Collections.emptyList());
            assertDoesNotThrow(() -> sqlExecutorService.execute(sql));
        }
    }
}

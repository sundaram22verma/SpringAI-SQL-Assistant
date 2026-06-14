package madhav.SpringAI;

import madhav.SpringAI.exception.SqlGenerationException;
import madhav.SpringAI.model.AiResponse;
import madhav.SpringAI.model.DatabaseType;
import madhav.SpringAI.model.SchemaInfo;
import madhav.SpringAI.service.impl.TextToSqlServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextToSqlServiceImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @InjectMocks
    private TextToSqlServiceImpl textToSqlService;

    private SchemaInfo schemaInfo;
    private DatabaseType databaseType;

    @BeforeEach
    void setUp() {
        schemaInfo = new SchemaInfo();
        SchemaInfo.TableInfo table = new SchemaInfo.TableInfo("public", "ai_services");
        table.addColumn(new SchemaInfo.ColumnInfo("id", "int"));
        table.addColumn(new SchemaInfo.ColumnInfo("name", "string"));
        table.addColumn(new SchemaInfo.ColumnInfo("provider", "string"));
        table.addColumn(new SchemaInfo.ColumnInfo("available", "boolean"));
        schemaInfo.addTable(table);
        databaseType = DatabaseType.MYSQL;
    }

    @Test
    void shouldReturnCorrectAiResponse() {
        String question = "Show all available AI services";
        String aiResponseText = """
                1. Intent Summary: Find all AI services that are currently available.
                2. Query Type: SELECT
                3. Generated SQL Query:
                ```sql
                SELECT * FROM ai_services WHERE available = true
                ```
                4. Risk Assessment: SAFE. This is a read-only query.
                5. Execution Status: ALLOWED
                """;

        when(chatClient.prompt().system(anyString()).user(anyString()).call().content()).thenReturn(aiResponseText);

        AiResponse result = textToSqlService.generateSql(question, schemaInfo, databaseType);

        assertNotNull(result);
        assertEquals("SELECT * FROM ai_services WHERE available = true", result.getSql());
        assertEquals("SELECT", result.getQueryType());
        assertEquals("ALLOWED", result.getExecutionStatus());
        assertTrue(result.getIntentSummary().contains("Find all AI services"));
    }

    @Test
    void shouldHandleBlockedQueries() {
        String question = "Delete all AI services";
        String aiResponseText = """
                1. Intent Summary: Delete all records from the ai_services table.
                2. Query Type: DELETE
                3. Generated SQL Query:
                ```sql
                DELETE FROM ai_services
                ```
                4. Risk Assessment: CRITICAL WARNING: This query will delete all rows.
                5. Execution Status: BLOCKED
                """;

        when(chatClient.prompt().system(anyString()).user(anyString()).call().content()).thenReturn(aiResponseText);

        AiResponse result = textToSqlService.generateSql(question, schemaInfo, databaseType);

        assertNotNull(result);
        assertEquals("DELETE FROM ai_services", result.getSql());
        assertEquals("DELETE", result.getQueryType());
        assertEquals("BLOCKED", result.getExecutionStatus());
    }

    @Test
    void shouldThrowExceptionWhenApiCallFails() {
        String question = "Show all AI services";
        String errorMessage = "API connection failed";

        when(chatClient.prompt().system(anyString()).user(anyString()).call())
                .thenThrow(new RuntimeException(errorMessage));

        SqlGenerationException ex = assertThrows(
                SqlGenerationException.class,
                () -> textToSqlService.generateSql(question, schemaInfo, databaseType)
        );

        assertTrue(ex.getMessage().contains("Failed to generate SQL"));
    }
}

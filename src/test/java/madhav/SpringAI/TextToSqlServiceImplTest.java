package madhav.SpringAI;

import madhav.SpringAI.exception.SqlGenerationException;
import madhav.SpringAI.service.impl.TextToSqlServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TextToSqlServiceImplTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @InjectMocks
    private TextToSqlServiceImpl textToSqlService;

    @Test
    void shouldReturnCorrectSqlQuery() {
        String question = "Show all available AI services";
        String expectedSql = "SELECT * FROM ai_services WHERE available = true";

        when(chatClient.prompt(anyString()).call().content()).thenReturn(expectedSql);

        String result = textToSqlService.generateSql(question);

        assertEquals(expectedSql, result);
        verify(chatClient).prompt(contains("QUESTION: Show all available AI services"));
    }

    @Test
    void shouldReturnSqlQueryWithComplexConditions() {
        String question = "Find all AI services by OpenAI launched after 2022";
        String expectedSql = "SELECT * FROM ai_services WHERE provider = 'OpenAI' AND launched_at > '2022-12-31'";

        when(chatClient.prompt(anyString()).call().content()).thenReturn(expectedSql);

        String result = textToSqlService.generateSql(question);

        assertEquals(expectedSql, result);
        verify(chatClient).prompt(contains("DATABASE SCHEMA"));
        verify(chatClient).prompt(contains("QUESTION: Find all AI services by OpenAI launched after 2022"));
    }

    @Test
    void shouldThrowExceptionWhenApiCallFails() {
        String question = "Show all AI services";
        String errorMessage = "API connection failed";

        when(chatClient.prompt(anyString()).call())
                .thenThrow(new RuntimeException(errorMessage));

        SqlGenerationException ex = assertThrows(
                SqlGenerationException.class,
                () -> textToSqlService.generateSql(question)
        );

        assertTrue(ex.getMessage().contains("Failed to generate SQL"));

        // <-- вот сюда
        verify(chatClient, atLeastOnce()).prompt(anyString());
    }


    @Test
    void shouldReturnSqlWithProperColumnNames() {
        String question = "Show names and providers of AI services";
        String expectedSql = "SELECT name, provider FROM ai_services";

        when(chatClient.prompt(anyString()).call().content()).thenReturn(expectedSql);

        String result = textToSqlService.generateSql(question);

        assertEquals(expectedSql, result);
        verify(chatClient).prompt(contains("Use clear and meaningful column names"));
    }

    @Test
    void shouldTrimExtraWhitespaceInReturnedSql() {
        String question = "Count AI services by provider";
        String rawSql = "\n\nSELECT provider, COUNT(*) as service_count FROM ai_services GROUP BY provider\n\n";
        String expectedSql = "SELECT provider, COUNT(*) as service_count FROM ai_services GROUP BY provider";

        when(chatClient.prompt(anyString()).call().content()).thenReturn(rawSql);

        String result = textToSqlService.generateSql(question);

        assertEquals(expectedSql, result);
    }
}

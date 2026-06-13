package madhav.SpringAI.service.impl;

import madhav.SpringAI.exception.SqlGenerationException;
import madhav.SpringAI.model.DatabaseType;
import madhav.SpringAI.model.SchemaInfo;
import madhav.SpringAI.service.TextToSqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class TextToSqlServiceImpl implements TextToSqlService {

    private static final Logger logger = LoggerFactory.getLogger(TextToSqlServiceImpl.class);
    private final ChatClient chatClient;

    public TextToSqlServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public String generateSql(String question, SchemaInfo schema, DatabaseType databaseType) {
        logger.info("Generating {} SQL for question: {}", databaseType.getDisplayName(), question);

        String prompt = """
                You are a %s expert. Convert the natural language question into a %s query.

                %s

                RULES:
                1. Use only the tables and columns from the provided schema.
                2. Return only the %s query, without explanations.
                3. Use SELECT queries only for safety.
                4. Use clear and meaningful column names in the output.
                5. Use %s-specific syntax if needed.

                QUESTION: %s

                %s Query:
                """.formatted(
                        databaseType.getDisplayName(), 
                        databaseType.getDisplayName(), 
                        schema.toString(),
                        databaseType.getDisplayName(),
                        databaseType.getDisplayName(),
                        question,
                        databaseType.getDisplayName()
                );

        try {
            var response = chatClient.prompt(prompt).call();
            String content = response.content();
            if (content == null || content.isBlank()) {
                throw new SqlGenerationException("Groq returned empty content");
            }
            String sql = content.trim();

            // Strip markdown code blocks if present
            if (sql.startsWith("```")) {
                sql = sql.replaceAll("(?s)^```(?:sql)?\\n?(.*?)\\n?```$", "$1").trim();
            }

            logger.debug("Generated SQL: {}", sql);
            return sql;
        } catch (SqlGenerationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating SQL: {}", e.getMessage(), e);
            throw new SqlGenerationException("Failed to generate SQL from question: " + e.getMessage(), e);
        }
    }
}

package madhav.SpringAI.service.impl;

import madhav.SpringAI.exception.SqlGenerationException;
import madhav.SpringAI.model.AiResponse;
import madhav.SpringAI.model.DatabaseType;
import madhav.SpringAI.model.SchemaInfo;
import madhav.SpringAI.service.TextToSqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextToSqlServiceImpl implements TextToSqlService {

    private static final Logger logger = LoggerFactory.getLogger(TextToSqlServiceImpl.class);
    private final ChatClient chatClient;

    public TextToSqlServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public AiResponse generateSql(String question, SchemaInfo schema, DatabaseType databaseType) {
        logger.info("Generating {} SQL for question: {}", databaseType.getDisplayName(), question);

        String systemPrompt = """
                You are a SQL Assistant.
                
                Your primary responsibility is to help users understand, generate, validate, optimize, and explain SQL queries. You must prioritize database safety at all times.
                
                ## Database Schema:
                %s
                
                ## Core Rules
                
                ### 1. Read-Only Queries
                The following query types are considered safe: SELECT, SHOW, DESCRIBE / DESC, EXPLAIN.
                For these queries:
                * Generate the SQL query.
                * Explain the query when necessary.
                * Mark them as SAFE.
                * Execution may be allowed by the calling application.
                
                ### 2. Data Modification Queries (DML)
                The following query types modify data: INSERT, UPDATE, DELETE, MERGE.
                For these queries:
                * Generate the correct SQL query.
                * DO NOT execute the query.
                * Clearly display a warning.
                * Inform the user that execution is disabled.
                * Provide a risk assessment.
                
                ### 3. Schema Modification Queries (DDL)
                The following query types modify database structure: CREATE, ALTER, DROP, TRUNCATE, RENAME.
                For these queries:
                * Generate the SQL query.
                * Do NOT execute.
                * Display a HIGH RISK warning.
                * Explain the potential impact.
                
                ### 4. Permission Management Queries (DCL)
                Includes: GRANT, REVOKE.
                For these queries:
                * Generate the SQL.
                * Never execute.
                * Warn about security implications.
                
                ### 5. Transaction Commands (TCL)
                Includes: COMMIT, ROLLBACK, SAVEPOINT, BEGIN, START TRANSACTION.
                For these queries:
                * Explain the command.
                * Generate SQL if requested.
                * Never execute.
                
                ## Safety Analysis Rules
                Always analyze generated queries and provide warnings when applicable (e.g., Missing WHERE clause in UPDATE/DELETE, DROP/TRUNCATE detection).
                
                ## Output Requirements
                For every SQL request return exactly this format:
                
                1. Intent Summary: [Briefly explain what the query does]
                2. Query Type: [SELECT/INSERT/UPDATE/DELETE/CREATE/etc]
                3. Generated SQL Query:
                ```sql
                [SQL Query Here]
                ```
                4. Risk Assessment: [Provide risk assessment and warnings]
                5. Execution Status: [ALLOWED or BLOCKED]
                
                Execution Status must always be:
                * ALLOWED (only for read-only queries)
                * BLOCKED (for all DML, DDL, DCL, and TCL operations)
                """.formatted(schema.toString());

        try {
            var response = chatClient.prompt()
                    .system(systemPrompt)
                    .user("Question: " + question + "\\nDatabase Type: " + databaseType.getDisplayName())
                    .call();
            
            String content = response.content();
            if (content == null || content.isBlank()) {
                throw new SqlGenerationException("AI returned empty content");
            }

            logger.debug("AI Response: {}", content);
            return parseAiResponse(content);
        } catch (SqlGenerationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating SQL: {}", e.getMessage(), e);
            throw new SqlGenerationException("Failed to generate SQL from question: " + e.getMessage(), e);
        }
    }

    private AiResponse parseAiResponse(String content) {
        AiResponse aiResponse = new AiResponse();
        
        // Use regex to extract parts
        aiResponse.setIntentSummary(extractPart(content, "1\\. Intent Summary:(.*?)(?=2\\. Query Type:|$)"));
        aiResponse.setQueryType(extractPart(content, "2\\. Query Type:(.*?)(?=3\\. Generated SQL Query:|$)"));
        
        // Extract SQL
        String sqlPart = extractPart(content, "3\\. Generated SQL Query:(.*?)(?=4\\. Risk Assessment:|$)");
        if (sqlPart.contains("```sql")) {
            sqlPart = sqlPart.replaceAll("(?s).*```sql\\n?(.*?)\\n?```.*", "$1").trim();
        } else if (sqlPart.contains("```")) {
            sqlPart = sqlPart.replaceAll("(?s).*```\\n?(.*?)\\n?```.*", "$1").trim();
        }
        aiResponse.setSql(sqlPart.trim());
        
        aiResponse.setRiskAssessment(extractPart(content, "4\\. Risk Assessment:(.*?)(?=5\\. Execution Status:|$)"));
        aiResponse.setExecutionStatus(extractPart(content, "5\\. Execution Status:(.*?)$"));

        // Fallback for status if parsing failed
        if (aiResponse.getExecutionStatus() == null || aiResponse.getExecutionStatus().isEmpty()) {
            if (content.contains("ALLOWED")) aiResponse.setExecutionStatus("ALLOWED");
            else aiResponse.setExecutionStatus("BLOCKED");
        } else {
            aiResponse.setExecutionStatus(aiResponse.getExecutionStatus().trim().toUpperCase());
            if (aiResponse.getExecutionStatus().contains("ALLOWED")) aiResponse.setExecutionStatus("ALLOWED");
            else if (aiResponse.getExecutionStatus().contains("BLOCKED")) aiResponse.setExecutionStatus("BLOCKED");
        }

        return aiResponse;
    }

    private String extractPart(String content, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}

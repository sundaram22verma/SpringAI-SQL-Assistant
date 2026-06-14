package madhav.SpringAI.service.impl;

import madhav.SpringAI.exception.EmptyQuestionException;
import madhav.SpringAI.model.AiResponse;
import madhav.SpringAI.model.QueryResult;
import madhav.SpringAI.service.SqlExecutorService;
import madhav.SpringAI.service.SqlQueryService;
import madhav.SpringAI.service.TextToSqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SqlQueryServiceImpl implements SqlQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryServiceImpl.class);
    private final TextToSqlService textToSqlService;
    private final SqlExecutorService sqlExecutorService;
    private final madhav.SpringAI.service.SchemaDiscoveryService schemaDiscoveryService;
    private final madhav.SpringAI.service.DataSourceManager dataSourceManager;

    public SqlQueryServiceImpl(TextToSqlService textToSqlService, SqlExecutorService sqlExecutorService, 
                               madhav.SpringAI.service.SchemaDiscoveryService schemaDiscoveryService,
                               madhav.SpringAI.service.DataSourceManager dataSourceManager) {
        this.textToSqlService = textToSqlService;
        this.sqlExecutorService = sqlExecutorService;
        this.schemaDiscoveryService = schemaDiscoveryService;
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public QueryResult processQuestion(String question) {
        logger.info("Processing question: {}", question);

        if (question == null || question.trim().isEmpty()) {
            throw new EmptyQuestionException();
        }

        long startTime = System.currentTimeMillis();
        
        // Dynamic schema discovery
        madhav.SpringAI.model.SchemaInfo schema = schemaDiscoveryService.getSchema();
        madhav.SpringAI.model.DatabaseType dbType = dataSourceManager.getCurrentConnection() != null ? 
                dataSourceManager.getCurrentConnection().getDatabaseType() : 
                madhav.SpringAI.model.DatabaseType.MYSQL; // Default if not explicitly connected

        AiResponse aiResponse = textToSqlService.generateSql(question, schema, dbType);
        
        List<List<String>> result = new ArrayList<>();
        if ("ALLOWED".equalsIgnoreCase(aiResponse.getExecutionStatus())) {
            result = sqlExecutorService.execute(aiResponse.getSql());
        } else {
            logger.info("Execution BLOCKED for query type: {}", aiResponse.getQueryType());
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Query processed in {} ms", executionTime);

        List<String> headers = result.isEmpty() ? Collections.emptyList() : result.get(0);
        List<List<String>> rows = result.size() > 1 ? result.subList(1, result.size()) : Collections.emptyList();

        return new QueryResult(
                aiResponse.getSql(),
                headers,
                rows,
                executionTime,
                aiResponse.getIntentSummary(),
                aiResponse.getQueryType(),
                aiResponse.getRiskAssessment(),
                aiResponse.getExecutionStatus()
        );
    }
}

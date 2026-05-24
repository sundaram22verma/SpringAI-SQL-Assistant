package madhav.SpringAI.service.impl;

import madhav.SpringAI.exception.EmptyQuestionException;
import madhav.SpringAI.model.QueryResult;
import madhav.SpringAI.service.SqlExecutorService;
import madhav.SpringAI.service.SqlQueryService;
import madhav.SpringAI.service.TextToSqlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SqlQueryServiceImpl implements SqlQueryService {

    private static final Logger logger = LoggerFactory.getLogger(SqlQueryServiceImpl.class);
    private final TextToSqlService textToSqlService;
    private final SqlExecutorService sqlExecutorService;

    public SqlQueryServiceImpl(TextToSqlService textToSqlService, SqlExecutorService sqlExecutorService) {
        this.textToSqlService = textToSqlService;
        this.sqlExecutorService = sqlExecutorService;
    }

    @Override
    public QueryResult processQuestion(String question) {
        logger.info("Processing question: {}", question);

        if (question == null || question.trim().isEmpty()) {
            throw new EmptyQuestionException();
        }

        long startTime = System.currentTimeMillis();
        String sql = textToSqlService.generateSql(question);
        List<List<String>> result = sqlExecutorService.execute(sql);
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("Query processed in {} ms", executionTime);

        if (result.isEmpty()) {
            return new QueryResult(sql, Collections.emptyList(), Collections.emptyList(), executionTime);
        }

        return new QueryResult(
                sql,
                result.get(0),
                result.size() > 1 ? result.subList(1, result.size()) : Collections.emptyList(), // строки данных
                executionTime
        );
    }
}
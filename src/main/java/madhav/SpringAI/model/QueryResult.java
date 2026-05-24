package madhav.SpringAI.model;

import java.util.List;


public class QueryResult {
    private final String sql;
    private final List<String> headers;
    private final List<List<String>> rows;
    private final long executionTimeMs;

    public QueryResult(String sql, List<String> headers, List<List<String>> rows, long executionTimeMs) {
        this.sql = sql;
        this.headers = headers;
        this.rows = rows;
        this.executionTimeMs = executionTimeMs;
    }

    public String getSql() {
        return sql;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public List<List<String>> getRows() {
        return rows;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public boolean hasResults() {
        return headers != null && !headers.isEmpty() && rows != null && !rows.isEmpty();
    }
}
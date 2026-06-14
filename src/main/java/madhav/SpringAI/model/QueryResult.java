package madhav.SpringAI.model;

import java.util.List;


public class QueryResult {
    private final String sql;
    private final List<String> headers;
    private final List<List<String>> rows;
    private final long executionTimeMs;
    private final String intentSummary;
    private final String queryType;
    private final String riskAssessment;
    private final String executionStatus;

    public QueryResult(String sql, List<String> headers, List<List<String>> rows, long executionTimeMs, 
                       String intentSummary, String queryType, String riskAssessment, String executionStatus) {
        this.sql = sql;
        this.headers = headers;
        this.rows = rows;
        this.executionTimeMs = executionTimeMs;
        this.intentSummary = intentSummary;
        this.queryType = queryType;
        this.riskAssessment = riskAssessment;
        this.executionStatus = executionStatus;
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

    public String getIntentSummary() {
        return intentSummary;
    }

    public String getQueryType() {
        return queryType;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public boolean hasResults() {
        return headers != null && !headers.isEmpty() && rows != null && !rows.isEmpty();
    }
}
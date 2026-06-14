package madhav.SpringAI.model;

public class AiResponse {
    private String intentSummary;
    private String queryType;
    private String sql;
    private String riskAssessment;
    private String executionStatus;

    public AiResponse() {}

    public AiResponse(String intentSummary, String queryType, String sql, String riskAssessment, String executionStatus) {
        this.intentSummary = intentSummary;
        this.queryType = queryType;
        this.sql = sql;
        this.riskAssessment = riskAssessment;
        this.executionStatus = executionStatus;
    }

    public String getIntentSummary() {
        return intentSummary;
    }

    public void setIntentSummary(String intentSummary) {
        this.intentSummary = intentSummary;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getRiskAssessment() {
        return riskAssessment;
    }

    public void setRiskAssessment(String riskAssessment) {
        this.riskAssessment = riskAssessment;
    }

    public String getExecutionStatus() {
        return executionStatus;
    }

    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }
}

package madhav.SpringAI.model;

import java.util.List;

/**
 * Represents dynamic statistics and suggestions for the dashboard.
 */
public class DashboardStats {
    private long totalTables;
    private long totalColumns;
    private List<String> suggestions;
    private String connectionStatus;

    public DashboardStats(long totalTables, long totalColumns, List<String> suggestions, String connectionStatus) {
        this.totalTables = totalTables;
        this.totalColumns = totalColumns;
        this.suggestions = suggestions;
        this.connectionStatus = connectionStatus;
    }

    public long getTotalTables() { return totalTables; }
    public long getTotalColumns() { return totalColumns; }
    public List<String> getSuggestions() { return suggestions; }
    public String getConnectionStatus() { return connectionStatus; }
}

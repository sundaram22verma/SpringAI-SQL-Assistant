package madhav.SpringAI.model;

import java.util.List;

public class DashboardStats {
    private long totalModels;
    private long recentModelsCount;
    private List<String> topProviders;
    private List<String> cheapestModels;

    public DashboardStats(long totalModels, long recentModelsCount, List<String> topProviders, List<String> cheapestModels) {
        this.totalModels = totalModels;
        this.recentModelsCount = recentModelsCount;
        this.topProviders = topProviders;
        this.cheapestModels = cheapestModels;
    }

    public long getTotalModels() { return totalModels; }
    public long getRecentModelsCount() { return recentModelsCount; }
    public List<String> getTopProviders() { return topProviders; }
    public List<String> getCheapestModels() { return cheapestModels; }

    public String getTopCheapestMessage() {
        if (cheapestModels == null || cheapestModels.isEmpty()) return "";
        return "Top 3 Cheapest (incl. " + cheapestModels.get(0) + ")";
    }

    public String getTopProvidersMessage() {
        if (topProviders == null || topProviders.isEmpty()) return "";
        return "Group by " + topProviders.size() + "+ providers";
    }
}

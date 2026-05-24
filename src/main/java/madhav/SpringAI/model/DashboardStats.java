package madhav.SpringAI.model;

import java.util.List;
import java.util.Map;

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
}

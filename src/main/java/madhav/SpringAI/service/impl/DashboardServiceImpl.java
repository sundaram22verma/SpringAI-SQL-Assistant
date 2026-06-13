package madhav.SpringAI.service.impl;

import madhav.SpringAI.model.DashboardStats;
import madhav.SpringAI.service.DashboardService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final madhav.SpringAI.service.DataSourceManager dataSourceManager;

    public DashboardServiceImpl(madhav.SpringAI.service.DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public DashboardStats getStats() {
        try {
            if (!tableExists("ai_services")) {
                return new DashboardStats(0L, 0L, List.of(), List.of());
            }
            
            JdbcTemplate jdbcTemplate = dataSourceManager.getJdbcTemplate();
            Long totalModels = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ai_services", Long.class);
            Long recentModels = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM ai_services WHERE launched_at > '2023-12-31'", Long.class);
            
            List<String> topProviders = jdbcTemplate.queryForList(
                    "SELECT provider FROM ai_services GROUP BY provider ORDER BY COUNT(*) DESC LIMIT 3", String.class);
            
            List<String> cheapestModels = jdbcTemplate.queryForList(
                    "SELECT name FROM ai_services ORDER BY input_price_per_1k_tokens ASC LIMIT 3", String.class);

            return new DashboardStats(
                    totalModels != null ? totalModels : 0,
                    recentModels != null ? recentModels : 0,
                    topProviders,
                    cheapestModels
            );
        } catch (Exception e) {
            return new DashboardStats(0L, 0L, List.of(), List.of());
        }
    }

    private boolean tableExists(String tableName) {
        try {
            dataSourceManager.getJdbcTemplate().execute("SELECT 1 FROM " + tableName + " LIMIT 1");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

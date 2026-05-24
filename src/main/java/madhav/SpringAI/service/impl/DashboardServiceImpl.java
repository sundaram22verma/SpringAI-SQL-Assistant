package madhav.SpringAI.service.impl;

import madhav.SpringAI.model.DashboardStats;
import madhav.SpringAI.service.DashboardService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DashboardStats getStats() {
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
    }
}

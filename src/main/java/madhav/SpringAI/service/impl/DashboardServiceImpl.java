package madhav.SpringAI.service.impl;

import madhav.SpringAI.model.DashboardStats;
import madhav.SpringAI.model.SchemaInfo;
import madhav.SpringAI.service.DashboardService;
import madhav.SpringAI.service.DataSourceManager;
import madhav.SpringAI.service.SchemaDiscoveryService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final DataSourceManager dataSourceManager;
    private final SchemaDiscoveryService schemaDiscoveryService;

    public DashboardServiceImpl(DataSourceManager dataSourceManager, SchemaDiscoveryService schemaDiscoveryService) {
        this.dataSourceManager = dataSourceManager;
        this.schemaDiscoveryService = schemaDiscoveryService;
    }

    @Override
    public DashboardStats getStats() {
        try {
            SchemaInfo schema = schemaDiscoveryService.getSchema();
            long totalTables = schema.getTables().size();
            long totalColumns = schema.getTables().stream()
                    .mapToLong(t -> t.getColumns().size())
                    .sum();

            List<String> suggestions = new ArrayList<>();
            if (totalTables > 0) {
                SchemaInfo.TableInfo firstTable = schema.getTables().get(0);
                suggestions.add("Explore all " + firstTable.getName() + " entries");
                
                if (totalTables > 1) {
                    suggestions.add("Show first 10 records from " + schema.getTables().get(1).getName());
                } else {
                    suggestions.add("Count total records in " + firstTable.getName());
                }

                if (!firstTable.getColumns().isEmpty()) {
                    suggestions.add("Group " + firstTable.getName() + " by " + firstTable.getColumns().get(0).getName());
                }
                
                suggestions.add("List all " + totalTables + " tables in database");
            } else {
                suggestions.add("No tables found. Check connection.");
            }

            String status = dataSourceManager.isConnected() ? "Connected" : "Disconnected";

            return new DashboardStats(totalTables, totalColumns, suggestions, status);
        } catch (Exception e) {
            return new DashboardStats(0L, 0L, List.of("Connect to a database to see stats"), "Error");
        }
    }
}

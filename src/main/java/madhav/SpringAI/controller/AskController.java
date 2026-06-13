package madhav.SpringAI.controller;

import madhav.SpringAI.model.QueryResult;
import madhav.SpringAI.service.SqlQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AskController {

    private static final Logger logger = LoggerFactory.getLogger(AskController.class);
    private final SqlQueryService sqlQueryService;
    private final madhav.SpringAI.service.DashboardService dashboardService;
    private final madhav.SpringAI.service.DataSourceManager dataSourceManager;
    private final madhav.SpringAI.service.SchemaDiscoveryService schemaDiscoveryService;

    public AskController(SqlQueryService sqlQueryService, 
                         madhav.SpringAI.service.DashboardService dashboardService,
                         madhav.SpringAI.service.DataSourceManager dataSourceManager,
                         madhav.SpringAI.service.SchemaDiscoveryService schemaDiscoveryService) {
        this.sqlQueryService = sqlQueryService;
        this.dashboardService = dashboardService;
        this.dataSourceManager = dataSourceManager;
        this.schemaDiscoveryService = schemaDiscoveryService;
    }

    @GetMapping("/")
    public String index(Model model) {
        logger.debug("Accessing index page");
        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("currentConnection", dataSourceManager.getCurrentConnection());
        model.addAttribute("schema", schemaDiscoveryService.getSchema());
        return "index";
    }

    @PostMapping("/")
    public String ask(@RequestParam String question, Model model) {
        logger.info("Received question: {}", question);
        model.addAttribute("question", question);
        model.addAttribute("stats", dashboardService.getStats());
        model.addAttribute("currentConnection", dataSourceManager.getCurrentConnection());
        model.addAttribute("schema", schemaDiscoveryService.getSchema());
        
        try {
            QueryResult result = sqlQueryService.processQuestion(question);
            model.addAttribute("sql", result.getSql());
            model.addAttribute("executionTime", result.getExecutionTimeMs());

            if (result.hasResults()) {
                model.addAttribute("headers", result.getHeaders());
                model.addAttribute("rows", result.getRows());
            } else {
                model.addAttribute("error", "There are no results for this request");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        }

        return "index";
    }
}

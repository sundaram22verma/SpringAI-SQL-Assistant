package madhav.SpringAI.controller;

import madhav.SpringAI.model.DatabaseConnection;
import madhav.SpringAI.service.DataSourceManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/database")
public class DatabaseController {

    private final DataSourceManager dataSourceManager;

    public DatabaseController(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @PostMapping("/connect")
    public String connect(@RequestParam String url,
                          @RequestParam String username,
                          @RequestParam String password,
                          @RequestParam String driver,
                          RedirectAttributes redirectAttributes) {
        try {
            DatabaseConnection connection = new DatabaseConnection(url, username, password, driver);
            dataSourceManager.connect(connection);
            redirectAttributes.addFlashAttribute("message", "Successfully connected to " + connection.getDatabaseType().getDisplayName());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to connect: " + e.getMessage());
        }
        return "redirect:/";
    }
}

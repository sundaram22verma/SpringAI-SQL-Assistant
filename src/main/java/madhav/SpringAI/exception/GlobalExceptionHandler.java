package madhav.SpringAI.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SqlGenerationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSqlGenerationException(SqlGenerationException ex, Model model) {
        model.addAttribute("error", "SQL Generation Error: " + ex.getMessage());
        return "index";
    }

    @ExceptionHandler(SqlExecutionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleSqlExecutionException(SqlExecutionException ex, Model model) {
        model.addAttribute("error", "SQL Execution Error: " + ex.getMessage());
        model.addAttribute("sql", ex.getSql());
        return "index";
    }

    @ExceptionHandler(EmptyQuestionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleEmptyQuestionException(EmptyQuestionException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "index";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", "Unexpected Error: " + ex.getMessage());
        return "index";
    }
}
package madhav.SpringAI.exception;

public class SqlExecutionException extends RuntimeException {

    private final String sql;

    public SqlExecutionException(String message, String sql) {
        super(message);
        this.sql = sql;
    }

    public SqlExecutionException(String message, String sql, Throwable cause) {
        super(message, cause);
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }
}
package madhav.SpringAI.model;

public enum DatabaseType {
    MYSQL("MySQL"),
    POSTGRESQL("PostgreSQL"),
    SQLSERVER("SQL Server"),
    H2("H2"),
    ORACLE("Oracle"),
    OTHER("Other");

    private final String displayName;

    DatabaseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DatabaseType fromUrl(String url) {
        if (url == null) return OTHER;
        String lowerUrl = url.toLowerCase();
        if (lowerUrl.contains("mysql")) return MYSQL;
        if (lowerUrl.contains("postgresql")) return POSTGRESQL;
        if (lowerUrl.contains("sqlserver")) return SQLSERVER;
        if (lowerUrl.contains("h2")) return H2;
        if (lowerUrl.contains("oracle")) return ORACLE;
        return OTHER;
    }
}

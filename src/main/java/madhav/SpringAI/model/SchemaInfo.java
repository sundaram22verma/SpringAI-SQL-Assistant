package madhav.SpringAI.model;

import java.util.ArrayList;
import java.util.List;

public class SchemaInfo {
    private List<TableInfo> tables = new ArrayList<>();

    public List<TableInfo> getTables() {
        return tables;
    }

    public void setTables(List<TableInfo> tables) {
        this.tables = tables;
    }

    public void addTable(TableInfo table) {
        this.tables.add(table);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DATABASE SCHEMA:\n");
        for (TableInfo table : tables) {
            String fullName = (table.getDatabase() != null ? table.getDatabase() + "." : "") + table.getName();
            sb.append("- ").append(fullName).append(" (");
            List<String> cols = new ArrayList<>();
            for (ColumnInfo col : table.getColumns()) {
                cols.add(col.getName() + ": " + col.getType());
            }
            sb.append(String.join(", ", cols)).append(")\n");
        }
        return sb.toString();
    }

    public static class TableInfo {
        private String database;
        private String name;
        private List<ColumnInfo> columns = new ArrayList<>();

        public TableInfo(String database, String name) {
            this.database = database;
            this.name = name;
        }

        public String getDatabase() {
            return database;
        }

        public String getName() {
            return name;
        }

        public List<ColumnInfo> getColumns() {
            return columns;
        }

        public void addColumn(ColumnInfo column) {
            this.columns.add(column);
        }
    }

    public static class ColumnInfo {
        private String name;
        private String type;

        public ColumnInfo(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}

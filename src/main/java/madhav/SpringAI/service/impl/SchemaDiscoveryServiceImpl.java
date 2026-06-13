package madhav.SpringAI.service.impl;

import madhav.SpringAI.model.SchemaInfo;
import madhav.SpringAI.service.DataSourceManager;
import madhav.SpringAI.service.SchemaDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Objects;

@Service
public class SchemaDiscoveryServiceImpl implements SchemaDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(SchemaDiscoveryServiceImpl.class);
    private final DataSourceManager dataSourceManager;

    public SchemaDiscoveryServiceImpl(DataSourceManager dataSourceManager) {
        this.dataSourceManager = dataSourceManager;
    }

    @Override
    public SchemaInfo getSchema() {
        SchemaInfo schemaInfo = new SchemaInfo();
        try (Connection connection = Objects.requireNonNull(dataSourceManager.getJdbcTemplate().getDataSource()).getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            String catalog = connection.getCatalog();
            String schema = connection.getSchema();
            
            logger.debug("Discovering schema for Catalog: {}, Schema: {}", catalog, schema);

            // Get tables - Scoped to current catalog and schema
            try (ResultSet tables = metaData.getTables(catalog, schema, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableCatalog = tables.getString("TABLE_CAT");
                    String tableSchema = tables.getString("TABLE_SCHEM");
                    String tableName = tables.getString("TABLE_NAME");
                    
                    // Skip system tables
                    if (tableName.startsWith("SYSTEM_") || tableName.startsWith("INFORMATION_SCHEMA") || tableName.startsWith("pg_")) continue;
                    
                    // Prefer catalog for MySQL, schema for others if available
                    String dbName = (tableSchema != null && !tableSchema.isEmpty()) ? tableSchema : tableCatalog;
                    
                    SchemaInfo.TableInfo tableInfo = new SchemaInfo.TableInfo(dbName);
                    
                    // Get columns for each table - Scoped to this specific table's catalog and schema
                    try (ResultSet columns = metaData.getColumns(tableCatalog, tableSchema, tableName, "%")) {
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String columnType = columns.getString("TYPE_NAME");
                            tableInfo.addColumn(new SchemaInfo.ColumnInfo(columnName, columnType));
                        }
                    }
                    schemaInfo.addTable(tableInfo);
                }
            }
        } catch (Exception e) {
            logger.error("Error discovering schema: {}", e.getMessage(), e);
        }
        return schemaInfo;
    }
}

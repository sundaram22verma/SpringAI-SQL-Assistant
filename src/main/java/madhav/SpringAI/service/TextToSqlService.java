package madhav.SpringAI.service;

import madhav.SpringAI.model.DatabaseType;
import madhav.SpringAI.model.SchemaInfo;

public interface TextToSqlService {
    String generateSql(String question, SchemaInfo schema, DatabaseType databaseType);
}

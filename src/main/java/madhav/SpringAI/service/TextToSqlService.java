package madhav.SpringAI.service;

import madhav.SpringAI.model.AiResponse;
import madhav.SpringAI.model.DatabaseType;
import madhav.SpringAI.model.SchemaInfo;

public interface TextToSqlService {
    AiResponse generateSql(String question, SchemaInfo schema, DatabaseType databaseType);
}

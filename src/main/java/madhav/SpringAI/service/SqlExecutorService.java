package madhav.SpringAI.service;

import java.util.List;

public interface SqlExecutorService {
    List<List<String>> execute(String sql);
}

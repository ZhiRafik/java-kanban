import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);
    void removeNode(Node node);
    ArrayList<Task> getHistory();
}

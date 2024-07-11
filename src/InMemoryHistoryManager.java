import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    ArrayList<Task> taskHistory = new ArrayList<>(); // в этом списке храним 10 тасков

    @Override
    public void add(Task task) {
        if (taskHistory.size() == 10) { // если тасков уже 10, удаляем самый старый
            taskHistory.removeFirst();
        }
        taskHistory.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return taskHistory;
    }
}

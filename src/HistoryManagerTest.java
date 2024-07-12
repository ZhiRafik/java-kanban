import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    private static int firstTaskID;
    private static TaskManager taskManager = Managers.getDefault();
    private HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeAll
    static void beforeAll() {
        Task firstTask = new Task("important", "getLunch", Status.IN_PROGRESS);
        Task secondTask = new Task("notImportant", "doHometasks", Status.IN_PROGRESS);
        Epic epic = new Epic("epicTest", "Test", Status.IN_PROGRESS);
        Subtask subtask = new Subtask(epic, "subtaskTest", "Test", Status.IN_PROGRESS);
        taskManager.createNewSubtask(subtask);
        taskManager.createNewEpic(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        firstTaskID = firstTask.getID();
    }

    @Test
    void add() {
        historyManager.add(taskManager.getTask(firstTaskID));
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
    }

}
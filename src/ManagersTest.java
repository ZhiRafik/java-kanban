import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ManagersTest {
    private static int firstTaskID;
    private static TaskManager taskManager = Managers.getDefault();

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
    void managersShouldReturnInitializedTaskManagers() {
        assertEquals(taskManager.getTask(firstTaskID).getID(), firstTaskID);
        //проверем, что задача проинициализирована и такой ID существует
    }
}
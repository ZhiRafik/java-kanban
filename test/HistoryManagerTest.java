import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    Task firstTask = new Task("important", "firstGetLunch", Status.IN_PROGRESS);
    Task secondTask = new Task("notImportant", "secondDoHometasks", Status.IN_PROGRESS);
    Epic epic = new Epic("epicTest", "eTest", Status.IN_PROGRESS);
    Subtask subtask = new Subtask(epic, "subtaskTest", "sTest", Status.IN_PROGRESS);
    private int firstTaskID;
    private int secondTaskID;
    private static TaskManager taskManager = Managers.getDefault();
    private static HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeEach
    void setUp() {
        taskManager.createNewSubtask(subtask);
        taskManager.createNewEpic(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        firstTaskID = firstTask.getID();
        secondTaskID = secondTask.getID();

    }

    @Test
    void add() {
        historyManager.add(firstTask);
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
    }

    @Test
    void historyShouldReturnRightOrder() {
        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(epic); // firstTask -> secondTask -> epic
        historyManager.add(secondTask); //  firstTask -> epic -> secondTask
        final ArrayList<Task> history = historyManager.getHistory();
        final String historyLine = history.get(0).taskDescription + " " + history.get(1).taskDescription + " "
                + history.get(2).taskDescription;
        final String expectedDescription = "secondDoHometasks eTest firstGetLunch";
        assertEquals(expectedDescription, historyLine);
    }

}
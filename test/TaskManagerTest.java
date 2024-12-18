import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private static int firstTaskID;
    private static int secondTaskID;
    private static int epicID;
    private static TaskManager taskManager = Managers.getDefault();

    @BeforeEach
    void setUp() {
        Task firstTask = new Task("important", "getLunch", Status.IN_PROGRESS);
        Task secondTask = new Task("notImportant", "doHometasks", Status.IN_PROGRESS);
        Epic epic = new Epic("epicTest", "Test", Status.IN_PROGRESS);
        Subtask subtask = new Subtask(epic, "subtaskTest", "Test", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic, "subtaskTest2", "Test2", Status.NEW);
        taskManager.createNewSubtask(subtask2);
        taskManager.createNewSubtask(subtask);
        taskManager.createNewEpic(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        firstTaskID = firstTask.getID();
        secondTaskID = secondTask.getID();
        epicID = epic.getID();
    }

    @Test
    void subtasksShouldBeRemovedWithTheirEpic() {
        int currentNumberOfSubtasks = taskManager.getAllSubtasks().size();
        taskManager.removeEpicByID(epicID); // epic имеет 2 subtask'a
        int numberOfSubtasksAfterRemove = taskManager.getAllSubtasks().size();
        assertEquals(currentNumberOfSubtasks - 2, numberOfSubtasksAfterRemove);
    }


    @Test
    void inMemoryTaskManagerActuallyCreatesTasksAndCanFindThemByID() {
        assertEquals("getLunch", taskManager.getTask(firstTaskID).taskDescription);
    } //проверяем, что описание задачи вызванной по первому ID соответствует описанию задачи с первым ID

    @Test
    void tasksShouldBeEqualIfIDsEqual() {
        assertEquals(taskManager.getTask(firstTaskID), taskManager.getTask(firstTaskID));
    }

    @Test
    void tasksShouldBeNotEqualIfIDsNotEqual() {
        assertNotEquals(taskManager.getTask(firstTaskID), taskManager.getTask(secondTaskID));
    }

    @Test
    void tasksShouldNotChangeTheirNameDescriptionAndStatusAfterAddingToManager() {
        String name = taskManager.getTask(firstTaskID).taskName;
        String description = taskManager.getTask(firstTaskID).taskDescription;
        String status = taskManager.getTask(firstTaskID).taskStatus.name();
        String thisInformation = name + description + status;
        String initialInformation = "important" + "getLunch" + "IN_PROGRESS";

        assertEquals(initialInformation, thisInformation);
    }

    @Test
    void subtaskShouldBeRemovedByID() {
        //создаём epic2, т.к subtask'и первого epic'а удаляются тестом allSubtasksShouldBeRemoved
        Epic epic2 = new Epic("epicTest2", "Test2", Status.IN_PROGRESS);
        Subtask subtask1 = new Subtask(epic2, "subtaskTest1", "Test1", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic2, "subtaskTest2", "Test2", Status.NEW);
        Subtask subtask3 = new Subtask(epic2, "subtaskTest3", "Test3", Status.IN_PROGRESS);
        taskManager.createNewEpic(epic2);
        taskManager.createNewSubtask(subtask3);
        taskManager.createNewSubtask(subtask2);
        taskManager.createNewSubtask(subtask1);
        //проводим сам тест:
        int subtask3ID = subtask3.getID();
        int numberOfSubtasksBeforeRemove = taskManager.getAllSubtasks().size();
        taskManager.removeSubtaskByID(subtask3ID);
        int numberOfSubtasksAfterRemove = taskManager.getAllSubtasks().size();
        assertEquals(numberOfSubtasksBeforeRemove - 1, numberOfSubtasksAfterRemove);
    }

    @Test
    void allSubtasksShouldBeRemoved() {
        taskManager.removeAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void allEpicsShouldBeRemoved() {
        taskManager.removeAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void oneEpicShouldBeRemoved() {
        Epic epic2 = new Epic("epicTest2", "Test2", Status.IN_PROGRESS);
        taskManager.createNewEpic(epic2);
        taskManager.removeEpicByID(epicID); //удаляем изначальный эпик
        assertEquals(1, taskManager.getAllEpics().size());
    }

}
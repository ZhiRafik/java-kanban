import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static int firstTaskID;
    private static int secondTaskID;
    private static int epicID;
    private static int subtaskID;
    static TaskManager taskManager = Managers.getDefault();
    HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeAll
    static void beforeEach() {
        Task firstTask = new Task("important", "getLunch", Status.IN_PROGRESS);
        Task secondTask = new Task("notImportant", "doHometasks", Status.IN_PROGRESS);
        Epic epic = new Epic("epicTest", "Test", Status.IN_PROGRESS);
        Subtask subtask = new Subtask(epic, "subtaskTest", "Test", Status.IN_PROGRESS);
        taskManager.createNewSubtask(subtask);
        taskManager.createNewEpic(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        subtaskID = subtask.getID();
        firstTaskID = firstTask.getID();
        secondTaskID = secondTask.getID();
        epicID = epic.getID();

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

//  @Test
//  void epicShouldNotBeAbleToAddItselfAsItsOwnSubtask() {
//      assertEquals(taskManager.getEpic(epicID).addSubtask(taskManager.getEpic(epicID)),
//              "You cannot add epic as it's own subtask");
//  } //компилятор сам не позволит добавить эпик в самого себя, поскольку необходим тип Subtask

//  @Test
//  void subtaskShouldSetItselfAsItsOwnEpic() {
//      taskManager.getSubtask(subtaskID).epic = taskManager.getSubtask(subtaskID);
//      } //компилятор сам не позволить заменить эпик сабтаска самим сабтаском, поскольку необходим тип Epic

    @Test
    void managersShouldReturnInitializedTaskManagers() {
        assertEquals(taskManager.getTask(firstTaskID).getID(), firstTaskID);
        //проверем, что задача проинициализирована и такой ID существует
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
    void add() {
        historyManager.add(taskManager.getTask(firstTaskID));
        final ArrayList<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
    }

    @Test
    void allSubtasksShouldBeRemoved() {
        taskManager.removeAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void AllEpicsShouldBeRemoved() {
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
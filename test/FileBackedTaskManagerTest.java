import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    private static int firstSubtaskID;
    private static int secondSubtaskID;
    private static int firstTaskID;
    private static int secondTaskID;
    private static int epicID;
    private static File tempFile;
    private static FileBackedTaskManager taskManager;

    @BeforeEach
    void setUp() {
        try {
            tempFile = File.createTempFile("tempFile", ".txt");
            tempFile.deleteOnExit();
        } catch (IOException e) {
            System.out.println("Ошибка при создании временного файла: " + e.getMessage());
        }
        taskManager = new FileBackedTaskManager(tempFile);

        Task firstTask = new Task("important", "getLunch", Status.IN_PROGRESS);
        Task secondTask = new Task("notImportant", "doHometasks", Status.IN_PROGRESS);
        secondTask.setDuration(Duration.ofMinutes(20));
        Epic epic = new Epic("epicTest", "Test", Status.IN_PROGRESS);
        Subtask subtask = new Subtask(epic, "subtaskTest", "Test", Status.IN_PROGRESS);
        subtask.setDuration(Duration.ofMinutes(15));
        Subtask subtask2 = new Subtask(epic, "subtaskTest2", "Test2", Status.NEW);
        subtask2.setStartTime(LocalDateTime.of(2024, 11, 24, 1, 46));
        taskManager.createNewSubtask(subtask2);
        taskManager.createNewSubtask(subtask);
        taskManager.createNewEpic(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        firstTaskID = firstTask.getID();
        secondTaskID = secondTask.getID();
        firstSubtaskID = subtask.getID();
        secondSubtaskID = subtask2.getID();
        epicID = epic.getID();
    }

    @Test
    void checkIfTasksAreLoadedFromFileCorrectly() {
        String originalTasksInFile = "1,SUBTASK,subtaskTest2,NEW,Test2,3,PT0S,2024-11-24T01:46," +
                "2,SUBTASK,subtaskTest,IN_PROGRESS,Test,3,PT15M,null," +
                "3,EPIC,epicTest,IN_PROGRESS,Test,PT0S,2024-11-24T01:46," +
                "4,TASK,important,IN_PROGRESS,getLunch,PT0S,null," +
                "5,TASK,notImportant,IN_PROGRESS,doHometasks,PT20M,null,";
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        String savedTasks = "";
        ArrayList<Task> tasks = manager2.getAllTasks();
        ArrayList<Subtask> subtasks = manager2.getAllSubtasks();
        ArrayList<Epic> epics = manager2.getAllEpics();
        for (Subtask i : subtasks) {
            savedTasks = savedTasks + i.toString();
        }
        for (Epic i : epics) {
            savedTasks = savedTasks + i.toString();
        }
        for (Task i : tasks) {
            savedTasks = savedTasks + i.toString();
        }
        assertEquals(originalTasksInFile, savedTasks);
    }

    @Test
    void checkIfSubtasksAreConnectedToEpicsCorrectly() {
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(taskManager.getEpic(epicID).getSubtasks(), manager2.getEpic(epicID).getSubtasks());
    }

    @Test
    void checkIfEpicsAreConnectedToSubtasksCorrectly() {
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(taskManager.getSubtask(firstSubtaskID).getEpic(), manager2.getSubtask(firstSubtaskID).getEpic());
    }
    @Test
    void checkIfTasksAreLoadedFromFileCorrectlyAfterDeletionATask() {
        taskManager.removeTaskByID(firstTaskID);
        String originalTasksInFile = "1,SUBTASK,subtaskTest2,NEW,Test2,3,PT0S,2024-11-24T01:46," +
                "2,SUBTASK,subtaskTest,IN_PROGRESS,Test,3,PT15M,null," +
                "3,EPIC,epicTest,IN_PROGRESS,Test,PT0S,2024-11-24T01:46," +
                "5,TASK,notImportant,IN_PROGRESS,doHometasks,PT20M,null,";
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        String savedTasks = "";
        ArrayList<Task> tasks = manager2.getAllTasks();
        ArrayList<Subtask> subtasks = manager2.getAllSubtasks();
        ArrayList<Epic> epics = manager2.getAllEpics();

        savedTasks = subtasks.stream()
                .map(Subtask::toString)
                .reduce("", (acc, str) -> acc + str);
        savedTasks = epics.stream()
                .map(Epic::toString)
                .reduce(savedTasks, (acc, str) -> acc + str);
        savedTasks = tasks.stream()
                .map(Task::toString)
                .reduce(savedTasks, (acc, str) -> acc + str);
        assertEquals(originalTasksInFile, savedTasks);
    }

    @Test
    void shouldNotAllowCreateTasksWithOverlap() {
        Task overlappingTask = new Task("overlapping", "toOverlap", Status.IN_PROGRESS);
        overlappingTask.setStartTime((LocalDateTime.of(2024, 11, 24, 1, 44)));
        //установлено время задачи за 2 минуты до subtask2
        overlappingTask.setDuration(Duration.ofMinutes(10)); // 8-минутное пересечение
        assertFalse(taskManager.prioritizedTasks.contains(overlappingTask));
        assertFalse(taskManager.tasks.containsValue(overlappingTask));
    }
}

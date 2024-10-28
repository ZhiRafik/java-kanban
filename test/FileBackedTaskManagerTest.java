import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
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
        Epic epic = new Epic("epicTest", "Test", Status.IN_PROGRESS);
        Subtask subtask = new Subtask(epic, "subtaskTest", "Test", Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic, "subtaskTest2", "Test2", Status.NEW);
        taskManager.createNewTask(subtask2);
        taskManager.createNewTask(subtask);
        taskManager.createNewTask(epic);
        taskManager.createNewTask(firstTask);
        taskManager.createNewTask(secondTask);
        firstTaskID = firstTask.getID();
        secondTaskID = secondTask.getID();
        epicID = epic.getID();
    }

    @Test
    void checkIfTasksAreAddedToFile() {
        String tasksInFile = "";
        String originalTasksInFile = "1,SUBTASK,subtaskTest2,NEW,Test2," +
                "2,SUBTASK,subtaskTest,IN_PROGRESS,Test," +
                "3,EPIC,epicTest,IN_PROGRESS,Test," +
                "4,TASK,important,IN_PROGRESS,getLunch," +
                "5,TASK,notImportant,IN_PROGRESS,doHometasks,";
        try {
            FileReader reader = new FileReader(tempFile);
            BufferedReader br = new BufferedReader(reader);
            while (br.ready()) {
                String line = br.readLine();
                tasksInFile = tasksInFile + line;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Ошибка при добавлении в файл: " + e.getMessage());
        }
        assertEquals(originalTasksInFile, tasksInFile);
    }

    @Test
    void chechIfTasksAreDeletedFromFile() {
        taskManager.removeTaskByID(firstTaskID);
        String tasksInFile = "";
        String originalTasksInFile = "1,SUBTASK,subtaskTest2,NEW,Test2," +
                "2,SUBTASK,subtaskTest,IN_PROGRESS,Test," +
                "3,EPIC,epicTest,IN_PROGRESS,Test," +
                "5,TASK,notImportant,IN_PROGRESS,doHometasks,"; //removed 4,TASK,important,IN_PROGRESS,getLunch,
        try {
            FileReader reader = new FileReader(tempFile);
            BufferedReader br = new BufferedReader(reader);
            while (br.ready()) {
                String line = br.readLine();
                tasksInFile = tasksInFile + line;
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Ошибка при добавлении в файл: " + e.getMessage());
        }
        assertEquals(originalTasksInFile, tasksInFile);
    }
}

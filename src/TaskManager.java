import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTask(int Id);

    Subtask getSubtask(int Id);

    Epic getEpic(int Id);

    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubtask(Subtask task);

    void removeTaskByID(int Id);

    void removeSubtaskByID(int Id);

    void removeEpicByID(int Id);

    ArrayList<Task> getHistory();

    ArrayList<Subtask> getSubtasksFromEpic(Epic epic);
}

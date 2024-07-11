import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void removeAllTasks();

    void removeAllSubtasks();

    void removeAllEpics();

    Task getTask(int ID);

    Subtask getSubtask(int ID);

    Epic getEpic(int ID);

    void createNewTask(Task task);

    void createNewEpic(Epic epic);

    void createNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubtask(Subtask task);

    void removeTaskByID(int ID);

    void removeSubtaskByID(int ID);

    void removeEpicByID(int ID);

    ArrayList<Task> getHistory();

    ArrayList<Subtask> getSubtasksFromEpic(Epic epic);

    int createNewID(int lastID);
}

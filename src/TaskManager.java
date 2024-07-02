import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int newID = 0; //сохраняем последний созданный ID, базовый - минимальное значение типа int

    public int createNewID(int lastID) { //вводим текущий ID в lastID
        if (lastID == 2_147_483_647){
            int newID = 0; //избегаем выхода за пределы int
        }
        newID++;
        return newID; //получили новый ID на единицу больше
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        allTasks.addAll(subtasks.values());
        allTasks.addAll(epics.values());
        return allTasks;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> set : epics.entrySet()) {
            Epic currentEpic = set.getValue();
            currentEpic.checkStatus();
            currentEpic.getSubtasks().clear();
        }
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Task getByID(int ID) {
        return(tasks.get(ID));
    }

    public void createNewTask(String taskName, String taskDescription, Status taskStatus) {
        newID = createNewID(newID);
        Task task = new Task(taskName, taskDescription, newID, taskStatus);
        tasks.put(newID, task);
    }

    public void createNewEpic(String epicName, String epicDescription, Status epicStatus) {
        newID = createNewID(newID);
        Epic epic = new Epic(epicName, epicDescription, newID, epicStatus);
        epics.put(newID, epic);
    }

    public void createNewSubtask(Epic epic, String subtaskName, String subtaskDescription, Status subtaskStatus) {
        newID = createNewID(newID);
        Subtask subtask = new Subtask(epic, subtaskName, subtaskDescription, newID, subtaskStatus);
        subtasks.put(newID, subtask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void updateEpic(Epic task) {
        epics.put(task.getID(), task);
    }

    public void updateSubtask(Subtask task) {
        subtasks.put(task.getID(), task);
        task.getEpic().checkStatus();
    }

    public void removeTaskByID(int ID) {
        if (tasks.containsKey(ID)) {
            tasks.remove(ID);
        }
    }

    public void removeSubtaskByID(int ID) {
        if (subtasks.containsKey(ID)) {
            int epicID = subtasks.get(ID).getEpic().getID(); //сохраняем id эпика сабтаска
            subtasks.remove(ID);
            epics.get(epicID).checkStatus(); //проверяем статус эпика по сохраненному id после удаления сабтаска
        }
    }

    public void removeEpicByID(int ID) {
        if (epics.containsKey(ID)) {
            for (Subtask subtask : epics.get(ID).getSubtasks()) { //получаем список сабтасков эпика и перебриваем его
                subtasks.remove(subtask.getID()); //удаляем сабтаск их хэшмэп сабтасков
            }
            epics.remove(ID);
        }
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtasks();
    }
}
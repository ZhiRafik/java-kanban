import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
public class TaskManager {
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    int newID = 0; //сохраняем последний созданный ID, базовый - минимальное значение типа int

    public int createNewID(int lastID) { //вводим текущий ID в lastID
        return newID++; //получили новый ID на единицу больше
    }

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        return allTasks;
    }

    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        allSubtasks.addAll(subtasks.values());
        return allSubtasks;
    }

    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        allEpics.addAll(epics.values());
        return allEpics;
    }

    public void removeAllTasks() {
        tasks.clear();
    }

    public void removeAllSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> set : epics.entrySet()) {
            Epic currentEpic = set.getValue();
            currentEpic.getSubtasks().clear();
            currentEpic.checkStatus();
        }
    }

    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public Task getByID(int ID) {
        return(tasks.get(ID));
    }

    public void createNewTask(Task task) {
        newID = createNewID(newID);
        task.setID(newID);
        tasks.put(newID, task);
    }

    public void createNewEpic(Epic epic) {
        newID = createNewID(newID);
        epic.setID(newID);
        epics.put(newID, epic);
    }

    public void createNewSubtask(Subtask subtask) {
        newID = createNewID(newID);
        subtask.setID(newID);
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
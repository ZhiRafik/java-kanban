import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
public class InMemoryTaskManager implements TaskManager {
    protected HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected int newID = 0; //сохраняем последний созданный ID, базовый - минимальное значение типа int

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(tasks.values());
        return allTasks;
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        ArrayList<Subtask> allSubtasks = new ArrayList<>();
        allSubtasks.addAll(subtasks.values());
        return allSubtasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> allEpics = new ArrayList<>();
        allEpics.addAll(epics.values());
        return allEpics;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.clear();
        for (Map.Entry<Integer, Epic> set : epics.entrySet()) {
            Epic currentEpic = set.getValue();
            currentEpic.getSubtasks().clear();
            currentEpic.checkStatus();
        }
    }

    @Override
    public void removeAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTask(int ID) {
        inMemoryHistoryManager.add(tasks.get(ID));
        return(tasks.get(ID));
    }

    @Override
    public Subtask getSubtask(int ID) {
        inMemoryHistoryManager.add(subtasks.get(ID));
        return(subtasks.get(ID));
    }

    @Override
    public Epic getEpic(int ID) {
        inMemoryHistoryManager.add(epics.get(ID));
        return(epics.get(ID));
    }

    @Override
    public void createNewTask(Task task) {
        newID = createNewID(newID);
        task.setID(newID);
        tasks.put(newID, task);
    }

    @Override
    public void createNewEpic(Epic epic) {
        newID = createNewID(newID);
        epic.setID(newID);
        epics.put(newID, epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        newID = createNewID(newID);
        subtask.setID(newID);
        subtasks.put(newID, subtask);
        subtask.getEpic().addSubtask(subtask); // добавляем в принадлежный эпик
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    @Override
    public void updateEpic(Epic task) {
        epics.put(task.getID(), task);
    }

    @Override
    public void updateSubtask(Subtask task) {
        subtasks.put(task.getID(), task);
        task.getEpic().checkStatus();
    }

    @Override
    public void removeTaskByID(int ID) {
        if (tasks.containsKey(ID)) {
            tasks.remove(ID);
        }
    }

    @Override
    public void removeSubtaskByID(int ID) {
        if (subtasks.containsKey(ID)) {
            int epicID = subtasks.get(ID).getEpic().getID(); //сохраняем id эпика сабтаска
            subtasks.remove(ID);
            epics.get(epicID).checkStatus(); //проверяем статус эпика по сохраненному id после удаления сабтаска
        }
    }

    @Override
    public void removeEpicByID(int ID) {
        if (epics.containsKey(ID)) {
            for (Subtask subtask : epics.get(ID).getSubtasks()) { //получаем список сабтасков эпика и перебриваем его
                subtasks.remove(subtask.getID()); //удаляем сабтаск из хэшмэп сабтасков
            }
            epics.remove(ID);
        }
    }

    @Override
    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    private int createNewID(int lastID) {
        newID++;
        return newID;
    }
}
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Comparator;

public class InMemoryTaskManager implements TaskManager {
    protected HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    protected int newID = 0; //сохраняем последний созданный id, базовый - минимальное значение типа int

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
        prioritizedTasks.removeAll(tasks.values());
        tasks.clear();
    }

    @Override
    public void removeAllSubtasks() {
        prioritizedTasks.removeAll(subtasks.values());
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
    public Task getTask(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return (tasks.get(id));
    }

    @Override
    public Subtask getSubtask(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));
        return (subtasks.get(id));
    }

    @Override
    public Epic getEpic(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return (epics.get(id));
    }

    @Override
    public void createNewTask(Task task) {
        boolean overlap = false;
        if (task.getStartTime() != null && prioritizedTasks.contains(task)
                && (checkTasksOverlap(prioritizedTasks.higher(task), task)
                || checkTasksOverlap(prioritizedTasks.lower(task), task))) {
            overlap = true;
        }
        if (!overlap) {
            newID = createNewID(newID);
            task.setID(newID);
            tasks.put(newID, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            System.out.println("TasksOverlap: " +  prioritizedTasks.lower(task).getEndTime()
                    + " -> " + task.getStartTime() + " " + task.getEndTime()
                    + " <- " + prioritizedTasks.higher(task).getStartTime());
        }
    }

    @Override
    public void createNewEpic(Epic epic) {
        newID = createNewID(newID);
        epic.setID(newID);
        epics.put(newID, epic);
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        boolean overlap = false;
        if (subtask.getStartTime() != null && prioritizedTasks.contains(subtask)
                && (checkTasksOverlap(prioritizedTasks.higher(subtask), subtask)
                || checkTasksOverlap(prioritizedTasks.lower(subtask), subtask))) {
            overlap = true;
        }
        if (!overlap) {
            newID = createNewID(newID);
            subtask.setID(newID);
            subtasks.put(newID, subtask);
            subtask.getEpic().addSubtask(subtask); // добавляем в принадлежный эпик
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        } else {
            System.out.println("TasksOverlap: " +  prioritizedTasks.lower(subtask).getEndTime()
                    + " -> " + subtask.getStartTime() + " " + subtask.getEndTime()
                    + " <- " + prioritizedTasks.higher(subtask).getStartTime());
        }
    }

    @Override
    public void updateTask(Task task) {
        boolean overlap = false;
        if (prioritizedTasks.contains(task) && (checkTasksOverlap(prioritizedTasks.higher(task), task)
                || checkTasksOverlap(prioritizedTasks.lower(task), task))) {
            overlap = true;
        }
        if (!overlap) {
            prioritizedTasks.remove(tasks.get(task.getID())); // может измениться время, соответственно и приоритет
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
            tasks.put(task.getID(), task);
        } else {
            System.out.println("TasksOverlap: " +  prioritizedTasks.lower(task).getEndTime()
                    + " -> " + task.getStartTime() + " " + task.getEndTime()
                    + " <- " + prioritizedTasks.higher(task).getStartTime());
        }
    }

    @Override
    public void updateEpic(Epic task) {
        epics.put(task.getID(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        boolean overlap = false;
        if (prioritizedTasks.contains(subtask) && (checkTasksOverlap(prioritizedTasks.higher(subtask), subtask)
                || checkTasksOverlap(prioritizedTasks.lower(subtask), subtask))) {
            overlap = true;
        }
        if (!overlap) {
            prioritizedTasks.remove(subtasks.get(subtask.getID())); // может измениться время, соответственно и приоритет
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
            subtasks.put(subtask.getID(), subtask);
            subtask.getEpic().checkStatus();
        } else {
            System.out.println("TasksOverlap: " +  prioritizedTasks.lower(subtask).getEndTime()
                    + " -> " + subtask.getStartTime() + " " + subtask.getEndTime()
                    + " <- " + prioritizedTasks.higher(subtask).getStartTime());
        }
    }

    @Override
    public void removeTaskByID(int id) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id).getStartTime() != null && prioritizedTasks.contains(tasks.get(id))) {
                prioritizedTasks.remove(tasks.get(id));
            }
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubtaskByID(int id) {
        if (subtasks.containsKey(id)) {
            int epicID = subtasks.get(id).getEpic().getID(); //сохраняем id эпика сабтаска
            if (subtasks.get(id).getStartTime() != null && prioritizedTasks.contains(subtasks.get(id))) {
                prioritizedTasks.remove(subtasks.get(id));
            }
            subtasks.remove(id);
            epics.get(epicID).checkStatus(); //проверяем статус эпика по сохраненному id после удаления сабтаска
        }
    }

    @Override
    public void removeEpicByID(int id) {
        if (epics.containsKey(id)) {
            for (Subtask subtask : epics.get(id).getSubtasks()) { //получаем список сабтасков эпика и перебираем его
                prioritizedTasks.remove(subtasks.get(subtask.getID()));
                subtasks.remove(subtask.getID()); //удаляем сабтаск из хэшмэп сабтасков
            }
            epics.remove(id);
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

    public boolean checkTasksOverlap(Task t1, Task t2) {
        if (t1 == null || t2 == null || t1.getEndTime() == null || t2.getEndTime() == null) {
            return false; // если у одного из объектов не указано время, пересечения быть не может
        }
        if (t1.getEndTime().isAfter(t2.getStartTime())
                || t2.getEndTime().isAfter(t1.getStartTime())) {
            return true;
        } else {
            return false;
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    private int createNewID(int lastID) {
        newID++;
        return newID;
    }
}
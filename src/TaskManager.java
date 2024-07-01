import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
public class TaskManager {
    Scanner scanner = new Scanner(System.in);
    int newID = -2_147_483_648; //сохраняем последний созданный ID, базовый - минимальное значение типа int
    public int createNewID(int lastID) { //вводим текущий ID в lastID
        if (lastID == 2_147_483_647){
            int newID = -2_147_483_648; //избегаем выхода за пределы int
        }
        newID++;
        return newID; //получили новый ID на единицу больше
    }

    HashMap<Integer, Task> tasks = new HashMap<>();

    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        for (int i = -2_147_483_648; i <= newID; i++) {
            allTasks.add(tasks.get(i)); //создаём списко со всеми задачами, чтобы вернуть все задачи списком
        }
        return allTasks;
    }

    public void removeAllTasks() {
        for (int i = -2_147_483_648; i <= newID; i++) {
            tasks.remove(i);
        }
    }

    public Task getByID(int ID) {
        return(tasks.get(ID));
    }

    public Task createNewTask() {
        System.out.println("Введите название задачи: ");
        String taskName = scanner.nextLine();
        System.out.println("Введите описание задачи: ");
        String taskDescription = scanner.nextLine();
        int newTaskID = createNewID(newID);
        Status status;
        while (true) {
            System.out.println("Введите статус задачи: 1 - NEW, 2 - IN_PROGRESS, 3 - DONE");
            int command = scanner.nextInt();
            if (command == 1) {
                status = Status.NEW;
                break;
            } else if (command == 2) {
                status = Status.IN_PROGRESS;
                break;
            } else if (command == 3) {
                status = Status.DONE;
                break;
            } else {
                System.out.println("Такого статуса нет");
            }
        }
        while (true) {
            System.out.println("Введите тип задачи: 1 - простая задача, 2 - задача с подпунктами, 3 - подпункт задачи");
            int command = scanner.nextInt();
            if (command == 1) {
                Task task = new Task(taskName, taskDescription, newTaskID, status);
                tasks.put(newTaskID, task);
                return task;
            } else if (command == 2) {
                Epic task = new Epic(taskName, taskDescription, newTaskID, status);
                tasks.put(newTaskID, task);
                return task;
            } else if (command == 3) {
                System.out.println("Введите название основной задачи: ");
                String epic = scanner.nextLine();
                boolean flag = false; //для вывода сообщения на случай, если эпика не существует
                for (int i = -2_147_483_648; i <= newID; i++) { //проверка на существование такого эпика
                    if (tasks.get(i).getClass() == Epic.class) {
                        // если имя задачи существует и принадлежит классу Epic, то
                        flag = true;
                        Subtask task = new Subtask((Epic) tasks.get(i), taskName, taskDescription, newTaskID, status);
                        task.getEpic().checkStatus();
                        tasks.put(newTaskID, task);
                        return task;
                    }
                }
                if (!flag) {
                    System.out.println("Такой основной задачи не существует");
                }
            } else {
                System.out.println("Такого типа задачи нет");
            }
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }
    public void updateTask(Epic task) {
        tasks.put(task.getID(), task);
    }
    public void updateTask(Subtask task) {
        tasks.put(task.getID(), task);
        task.getEpic().checkStatus();
    }

    public void removeByID (int ID) {
        if (tasks.containsKey(ID)) {
            if (tasks.get(ID).getClass() == Epic.class) {
                ((Subtask) tasks.get(ID)).getEpic().checkStatus();
                // проверка на удаление подпунтка их эпика, удаление которого
                // может привести эпик к полному выполнению
            }
            tasks.remove(ID);

        } else {
            System.out.println("Такого идентификатора не существует");
        }
    }

    public ArrayList<Subtask> getSubtasksFromEpic(Epic epic) {
        return epic.getSubtasks();
    }
}
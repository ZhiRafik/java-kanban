import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;

public class FileBackedTaskManager extends InMemoryTaskManager {
    protected File file;
    String filePath;

    public FileBackedTaskManager(File file) {
        if (file == null || !file.exists()) {
            throw new
                    IllegalArgumentException("Файл не существует или является null при создании FileBackedTaskManager");
        }
        this.file = file;
        filePath = file.getPath();
    }

    static FileBackedTaskManager loadFromFile(File file) {
        try {
            if (file == null || !file.exists()) {
                throw new IllegalArgumentException("Файл не найден или является null при восстановлении задач");
            }
            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            ArrayList<String> epics = new ArrayList<>();
            ArrayList<String> subtasks = new ArrayList<>();
            while (br.ready()) {
                String line = br.readLine(); // отделили задачу
                String[] split = line.split(",");
                if (split[1].equals(Type.TASK.toString())) {
                    Task task = fromString(line);
                    task.setID(Integer.parseInt(split[0]));
                    fileBackedTaskManager.tasks.put(Integer.parseInt(split[0]), task);
                } else if (split[1].equals(Type.EPIC.toString())) {
                    epics.add(line);
                } else if (split[1].equals(Type.SUBTASK.toString())) {
                    subtasks.add(line);
                }
            }
            for (String line : epics) {
                String[] split = line.split(",");
                if (split[1].equals(Type.EPIC.toString())) {
                    Epic epic = (Epic) fromString(line);
                    epic.setID(Integer.parseInt(split[0]));
                    fileBackedTaskManager.epics.put(Integer.parseInt(split[0]), epic);
                }
            }
            for (String line : subtasks) {
                String[] split = line.split(",");
                if (split[1].equals(Type.SUBTASK.toString())) {
                    Subtask subtask = (Subtask) fromString(line);
                    subtask.setID(Integer.parseInt(split[0]));
                    subtask.setEpic(fileBackedTaskManager.epics.get(Integer.parseInt(split[5])));
                    fileBackedTaskManager.subtasks.put(Integer.parseInt(split[0]), subtask);
                }
            }
            br.close();
            return fileBackedTaskManager;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createNewTask(Task task) {
        super.createNewTask(task);
        save();
    }

    @Override
    public void createNewEpic(Epic epic) {
        super.createNewEpic(epic);
        save();
    }

    @Override
    public void createNewSubtask(Subtask subtask) {
        super.createNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic task) {
        super.updateEpic(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask task) {
        super.updateSubtask(task);
        save();
    }

    @Override
    public void removeTaskByID(int id) {
        super.removeTaskByID(id);
        save();
    }

    @Override
    public void removeSubtaskByID(int id) {
        super.removeSubtaskByID(id);
        save();
    }

    @Override
    public void removeEpicByID(int id) {
        super.removeEpicByID(id);
        save();
    }

    private void save() {
        try (FileWriter cleaningFileWriter = new FileWriter(filePath, false)) {
            cleaningFileWriter.write("id,type,name,status,description,epic"); //перезаписываем файл
        } catch (IOException e) {
            throw new IllegalArgumentException("ManagerSaveException: " + e.getMessage());
        }
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("ManagerSaveException: file is null or does not exist");
        }
        ArrayList<Task> tasksToSave = getAllTasks();
        ArrayList<Epic> epicsToSave = getAllEpics();
        ArrayList<Subtask> subtasksToSave = getAllSubtasks();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (Task i : tasksToSave) {
                fileWriter.write(toString(i) + System.lineSeparator());
            }
            for (Epic i : epicsToSave) {
                fileWriter.write(toString(i) + System.lineSeparator());
            }
            for (Subtask i : subtasksToSave) {
                fileWriter.write(toString(i) + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("ManagerSaveException: " + e.getMessage());
        }
    }

    private static Task fromString(String line) {
        String[] split = line.split(","); //разобрали на поля
        if (split[1].equals("TASK")) {
            Status status = Status.valueOf(split[3]);
            return new Task(split[2], split[4], status);
        } else if (split[1].equals("EPIC")) {
            Status status = Status.valueOf(split[3]);
            return new Epic(split[2], split[4], status);
        } else if (split[1].equals("SUBTASK")) {
            Status status = Status.valueOf(split[3]);
            return new Subtask(null, split[2], split[4], status); // невозможно восстановить привязку к Epic
                                            // из-за статичности метода, нужно использовать нестатичную переменную
        }
        return null;
    }

    private String toString(Task task) {
        return task.toString();
    }
}
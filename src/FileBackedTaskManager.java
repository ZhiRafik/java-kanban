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
        fileBackedTaskManagerLoadFromFile();
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

    public void save() {
        try (FileWriter cleaningFileWriter = new FileWriter(filePath, false)) {
            cleaningFileWriter.write("id,type,name,status,description,epic"); //перезаписываем файл
        } catch (IOException e) {
            System.out.println("Произошла ошибка при очистке файла: " + e.getMessage());
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
            System.out.println("Произошла ошибка при записи в файл: " + e.getMessage());
        }
    }

    public void fileBackedTaskManagerLoadFromFile() {
        try {
            if (file == null || !file.exists()) {
                throw new IllegalArgumentException("Файл не найден или является null при восстановлении задач");
            }
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            while (br.ready()) {
                String line = br.readLine(); // отделили задачу
                fromString(line);
            }
            br.close();
        } catch (IOException e) {
            System.out.println("Ошибка восстановления задач: " + e.getMessage());
        }
    }

    public void fromString(String line) {
        String[] split = line.split(","); //разобрали на поля
        if (split[1].equals("TASK")) {
            Status status = Status.valueOf(split[3]);
            Task task = new Task(split[2], split[4], status);
            tasks.put(Integer.parseInt(split[0]), task);
        } else if (split[1].equals("EPIC")) {
            Status status = Status.valueOf(split[3]);
            Epic epic = new Epic(split[2], split[4], status);
            epics.put(Integer.parseInt(split[0]), epic);
        } else if (split[1].equals("SUBTASK")) {
            Status status = Status.valueOf(split[3]);
            Subtask subtask = new Subtask(epics.get(split[5]), split[2], split[4], status);
            subtasks.put(Integer.parseInt(split[0]), subtask);
        }
    }

    String toString(Subtask task) {
        String taskString = (task.getID() + ",") + (task.getType() + ",") + (task.getName() + ",") +
                (task.getStatus() + ",") + (task.getDescription() + "," + (task.getEpic().getID() + ","));
        return taskString;
    }

    String toString(Task task) {
        String taskString = (task.getID() + ",") + (task.getType() + ",") + (task.getName() + ",") +
                (task.getStatus() + ",") + (task.getDescription() + ",");
        return taskString;
    }
}
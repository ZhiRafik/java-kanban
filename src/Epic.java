import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(String taskName, String taskDescription, Status taskStatus) {
        super(taskName, taskDescription, taskStatus);
        this.taskStatus = taskStatus;
        this.type = Type.EPIC;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        if (subtask.getID() == this.taskID) {
            throw new IllegalArgumentException("You cannot add epic as it's own subtask");
        }
        subtasks.add(subtask);
    }

    public void updateSubtask(Subtask updatedSubtask) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).taskID == updatedSubtask.taskID) {
                subtasks.set(i, updatedSubtask);
            }
        }
        checkStatus();
    }

    public void checkStatus() {
        //проверяем изменение статутса всего эпика
        boolean newStatus = true;
        boolean doneStatus = true;
        for (Subtask subtask : subtasks) {
            if (subtask.taskStatus != Status.NEW) {
                newStatus = false;
            }
            if (subtask.taskStatus != Status.DONE) {
                doneStatus = false;
            }
        }
        if (newStatus) taskStatus = Status.NEW;
        else if (doneStatus) taskStatus = Status.DONE;
        else taskStatus = Status.IN_PROGRESS;
    }
}

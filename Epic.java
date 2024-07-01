import java.util.ArrayList;
public class Epic extends Task{
    ArrayList<Subtask> subtasks = new ArrayList<>();
    private Status taskStatus;
    public Epic(String taskName, String taskDescription, int taskID, Status taskStatus) {
        super(taskName, taskDescription, taskID, taskStatus);
        this.taskStatus = taskStatus;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void printSubtasks() {
        System.out.println(subtasks.toString());
    }

    public void addSubtask(Subtask subtask) {
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

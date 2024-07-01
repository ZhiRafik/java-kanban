public class Subtask extends Task {
    Epic epic;

    public Subtask (Epic epic, String taskName, String taskDescription, int taskID, Status taskStatus) {
        super(taskName, taskDescription, taskID, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

}

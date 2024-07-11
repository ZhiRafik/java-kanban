public class Subtask extends Task {
    Epic epic;
    int taskID;

    public Subtask (Epic epic, String taskName, String taskDescription, Status taskStatus) {
        super(taskName, taskDescription, taskStatus);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

}

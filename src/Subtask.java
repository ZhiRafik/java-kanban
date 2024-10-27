public class Subtask extends Task {
    private Epic epic;

    public Subtask(Epic epic, String taskName, String taskDescription, Status taskStatus) {
        super(taskName, taskDescription, taskStatus);
        this.epic = epic;
        this.type = Type.SUBTASK;
    }

    public Epic getEpic() {
        return epic;
    }

}

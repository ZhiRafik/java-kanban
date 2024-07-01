public class Task {
    String taskName;
    String taskDescription;
    int taskID;
    Status taskStatus;

    public Task (String taskName, String taskDescription, int taskID, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskID = taskID;
        this.taskStatus = taskStatus;
    }

    public int getID () {
        return taskID;
    }
}

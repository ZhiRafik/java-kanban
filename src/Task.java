public class Task {
    String taskName;
    String taskDescription;
    int taskID;
    Status taskStatus;

    public Task (String taskName, String taskDescription, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public int getID () {
        return taskID;
    }

    public void setID(int ID) {
        this.taskID = ID;
    }
}

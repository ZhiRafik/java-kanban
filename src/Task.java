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

    @Override // перепишем метод эквивалентности объектов для тестировки
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskName.equals(task.taskName) && taskDescription.equals(task.taskDescription) && taskID == task.getID()
                && taskStatus.equals(task.taskStatus);
    }
}

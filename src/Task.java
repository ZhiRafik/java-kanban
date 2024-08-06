public class Task {
    protected String taskName;
    protected String taskDescription;
    protected int taskID;
    protected Status taskStatus;

    public Task(String taskName, String taskDescription, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
    }

    public int getID() {
        return taskID;
    }

    public void setID(int id) {
        this.taskID = id;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class Task {
    protected String taskName;
    protected String taskDescription;
    protected int taskID;
    protected Status taskStatus;
    protected Type type;
    protected Duration duration = Duration.ZERO;
    protected LocalDateTime startTime;


    public Task(String taskName, String taskDescription, Status taskStatus) {
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.type = Type.TASK;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return Optional.ofNullable(startTime)
                .map(time -> time.plus(duration))
                .orElse(null);
    }

    public String getDescription() {
        return taskDescription;
    }

    public Status getStatus() {
        return taskStatus;
    }

    public Type getType() {
        return type;
    }

    public int getID() {
        return taskID;
    }

    public void setID(int id) {
        this.taskID = id;
    }

    public String getName() {
        return taskName;
    }

    @Override
    public String toString() {
        return taskID + "," + type + "," + taskName + "," + taskStatus + "," + taskDescription + ","
                + duration + "," + startTime + ",";
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

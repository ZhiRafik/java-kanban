import java.util.ArrayList;
import java.time.LocalDateTime;

public class Epic extends Task {
    private ArrayList<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

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
        if (endTime == null && subtask.getEndTime() != null) {
            endTime = subtask.getEndTime();
        } else if (endTime != null && subtask.getDuration() != null) {
            endTime.plus(subtask.getDuration());
        }
        if (subtask.getDuration() != null) {
            duration.plus(subtask.getDuration());
        }
        if (subtask.getStartTime() != null
                && (startTime == null || subtask.getStartTime().isBefore(startTime))) {
            startTime = subtask.getStartTime(); // обновляем время начало epic'a, если новая подзадача начинается раньше
        }
    }

    public void checkStatus() {
        //проверяем изменение статутса всего эпика
        boolean newStatus = subtasks.stream()
                .filter(subtask -> subtask.taskStatus != Status.NEW)
                .findAny()
                .isEmpty();
        boolean doneStatus = subtasks.stream()
                .filter(subtask -> subtask.taskStatus != Status.DONE)
                .findAny()
                .isEmpty();
        if (newStatus) taskStatus = Status.NEW;
        else if (doneStatus) taskStatus = Status.DONE;
        else taskStatus = Status.IN_PROGRESS;
    }
}

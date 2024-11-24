import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Optional;

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

    public void updateSubtask(Subtask updatedSubtask) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).taskID == updatedSubtask.taskID) {
                duration.minus(subtasks.get(i).getDuration()); // в обновлённой подзадаче может быть изменено время
                endTime.minus(subtasks.get(i).getDuration());
                subtasks.set(i, updatedSubtask);
                duration.plus(subtasks.get(i).getDuration());
                endTime.plus(subtasks.get(i).getDuration());
                if (subtasks.get(i).getStartTime().isBefore(startTime)) { // обновляем время начало epic'a,
                    startTime = subtasks.get(i).getStartTime(); // если новая подзадача начинается раньше
                }
            }
        }
        endTime = startTime.plus(duration); // также обновляем время окончания
        checkStatus();
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

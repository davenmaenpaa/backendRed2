package se.backend.groupred2.model.Task;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public final class TaskStatusDate {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    protected TaskStatusDate() {}

    public TaskStatusDate(Task task, LocalDate date, TaskStatus status) {
        this.task = task;
        this.date = date;
        this.status = status;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

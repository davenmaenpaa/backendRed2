package se.backend.groupred2.model;

import se.backend.groupred2.model.Task.Task;

import javax.persistence.*;

@Entity
public final class Issue {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Task task;

    @Column(nullable = false)
    private String title, description;

    protected Issue() {}

    public Issue(long id, Task task, String title, String description) {
        this.id = id;
        this.task = task;
        this.title = title;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }

    public String getDescription() {
        return description;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

package se.backend.groupred2.model.Task;

import com.fasterxml.jackson.annotation.JsonBackReference;
import se.backend.groupred2.model.User;

import javax.persistence.*;

@Entity
public final class Task {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title, description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private User user;

    protected Task() {}

    public Task(String title, String description, TaskStatus status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }

    public Task(TaskStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", user=" + user +
                '}';
    }
}

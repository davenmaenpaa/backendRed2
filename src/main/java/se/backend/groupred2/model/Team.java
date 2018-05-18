package se.backend.groupred2.model;

import javax.persistence.*;

@Entity
public final class Team {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, columnDefinition = "int default 10")
    private int maxUsers;

    protected Team() {}

    public Team(String name, boolean active, int maxUsers) {
        this.name = name;
        this.active = active;
        this.maxUsers = maxUsers;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void deActivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public void setName(String name) {
        this.name = name;
    }
}

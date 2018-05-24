package se.backend.groupred2.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public final class User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName, lastName, userName;

    private boolean active;

    @Column(nullable = false, unique = true)
    private Long userNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    private Team team;

    protected User() {}

    public User(String firstName, String lastName, String userName, boolean active, Long userNumber) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.active = active;
        this.userNumber = userNumber;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return active;
    }

    public Long getUserNumber() {
        return userNumber;
    }

    public Team getTeam() {
        return team;
    }

    public User setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public User setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public User setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public User setActive(boolean active) {
        this.active = active;
        return this;
    }

    public User setUserNumber(Long userNumber) {
        this.userNumber = userNumber;
        return this;
    }

    public void deActivate() {
        this.active = false;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}

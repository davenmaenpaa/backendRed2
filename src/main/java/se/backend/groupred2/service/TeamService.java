package se.backend.groupred2.service;

import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Team;
import se.backend.groupred2.model.User;
import se.backend.groupred2.repository.TeamRepository;
import se.backend.groupred2.repository.UserRepository;
import se.backend.groupred2.service.exceptions.InvalidInputException;
import se.backend.groupred2.service.exceptions.InvalidTeamException;
import se.backend.groupred2.service.exceptions.InvalidUserException;

import java.util.Optional;

@Service
public final class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public Optional<Team> deActivate(Team team) {
        Optional<Team> result = teamRepository.findById(team.getId());

        result.ifPresent(t -> {
            checkIfActive(t);
            t.deActivate();
            teamRepository.save(result.get());
        });

        return result;
    }

    private void checkIfActive(Team team) {
        if (!team.isActive())
            throw new InvalidTeamException("Team is already inactive.");
    }

    public Optional<Team> update(Team team) {
        Optional<Team> result = teamRepository.findById(team.getId());

        result.ifPresent(t -> {
            t.setName(team.getName());
            teamRepository.save(result.get());
        });

        return result;
    }

    public Iterable<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<User> addUser(Long teamId, Long userId) {
        Optional<Team> teamResult = teamRepository.findById(teamId);
        Optional<User> userResult = userRepository.findById(userId);

        if (!teamResult.isPresent() && !userResult.isPresent()) {
            throw new InvalidInputException("Team and User does not exist");

        } else if (!teamResult.isPresent()) {
            throw new InvalidTeamException("Team does not exist.");

        } else if (!userResult.isPresent()) {
            throw new InvalidUserException("User does not exist");

        } else {
            User user = userResult.get();
            Team team = teamResult.get();

            validate(team, user);
            user.setTeam(team);

            userRepository.save(user);
        }

        return userResult;
    }

    private void validate(Team team, User user) {
        if (userRepository.countByTeam(team) >= team.getMaxUsers()) {
            throw new InvalidTeamException("Can't add user. Team is full");

        } else if (user.getTeam().getId().equals(team.getId())) {
            throw new InvalidTeamException("User is already in that team.");
        }
    }
}


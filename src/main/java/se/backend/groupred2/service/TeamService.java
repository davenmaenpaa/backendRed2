package se.backend.groupred2.service;

import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Team;
import se.backend.groupred2.model.User;
import se.backend.groupred2.repository.TeamRepository;
import se.backend.groupred2.repository.UserRepository;
import se.backend.groupred2.service.exceptions.*;

import java.util.List;
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
        team.setMaxUsers(10);

        return teamRepository.save(team);
    }

    public Optional<Team> deActivate(Long teamId) {
        Optional<Team> result = teamRepository.findById(teamId);

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

    public Optional<Team> update(Long teamId, Team team) {
        Optional<Team> result = teamRepository.findById(teamId);

        result.ifPresent(t -> {
            t.setName(team.getName());
            teamRepository.save(result.get());
        });

        return result;
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamRepository.findAll();

        if (teamRepository.findAll().isEmpty())
            throw new NoContentException();

        return teams;
    }

    public Team getTeam(Long teamId) {
        Optional<Team> result = teamRepository.findById(teamId);
        Team team;

        if(result.isPresent())
            team = result.get();
        else
            throw new InvalidTeamException("Team does not exist.");

        return team;
    }

    public Optional<User> addUserToTeam(Long teamId, Long userId) {
        Optional<Team> teamResult = teamRepository.findById(teamId);
        Optional<User> userResult = userRepository.findById(userId);

        if(teamResult.isPresent() && userResult.isPresent()) {
            User user = userResult.get();
            Team team = teamResult.get();


            validate(team);
            user.setTeam(team);

            userRepository.save(user);

        } else if(!teamResult.isPresent() && !userResult.isPresent()) {
            throw new InvalidInputException();

        } else if(!teamResult.isPresent()) {
            throw new InvalidTeamException("Team does not exist.");

        } else
            throw new InvalidUserException("User does not exist");

        return userResult;
    }

    private void validate(Team team) {
        if (userRepository.countByTeam(team) >= team.getMaxUsers())
            throw new InvalidTeamException("Can't add user. Team is full");
    }
}


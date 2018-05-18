package se.backend.groupred2.repository;

import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Team;
import se.backend.groupred2.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
    int countByTeam(Team team);

    List<User> findByUserNumber(Long userNumber);

    List<User> findUserByUserName(String userName);

    List<User> findUserByFirstName(String firstName);

    List<User> findUserByLastName(String lastName);

    //List<User> findAllUserByTeamId(Long id);

    List<User> findUsersByTeamId(Long teamId);

    List<User> findAll();
}



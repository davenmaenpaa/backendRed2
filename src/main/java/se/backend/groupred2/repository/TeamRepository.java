package se.backend.groupred2.repository;

import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Team;

import java.util.List;

public interface TeamRepository extends CrudRepository<Team, Long> {
    List<Team> findAll();
}

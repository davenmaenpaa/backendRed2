package se.backend.groupred2.repository;

import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Team;

public interface TeamRepository extends CrudRepository<Team, Long> {}

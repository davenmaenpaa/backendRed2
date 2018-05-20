package se.backend.groupred2.repository;

import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Task.TaskStatusDate;

public interface TaskStatusRepository extends CrudRepository<TaskStatusDate, Long> {}

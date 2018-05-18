package se.backend.groupred2.repository;

import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {
    List<Task> findAllTaskByUserId(Long userId);

    List<Task> findAllByStatus(Enum status);

    List<Task> findAll();

    List<Task> findAllByUser_Id(Long userId);
}

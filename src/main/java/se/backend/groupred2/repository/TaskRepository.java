package se.backend.groupred2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Task;

import java.util.List;

public interface TaskRepository extends CrudRepository<Task, Long> {

    List<Task> findAllTaskByUserId(Long userId);

    Page<Task> findAllTaskByUserId(Long userId, Pageable pageable);

    Page<Task> findAllByStatus(Enum status, Pageable pageable);

    Page<Task> findAll(Pageable pageable);

    List<Task> findAllByUser_Id(Long userId);
}

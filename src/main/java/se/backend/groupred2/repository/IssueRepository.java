package se.backend.groupred2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task;

import java.awt.print.Pageable;
import java.util.List;

public interface IssueRepository extends CrudRepository<Issue, Long> {

    @Query("SELECT DISTINCT i.task FROM Issue i")
    List<Task> findDistinctOnTask();

    @Query("SELECT DISTINCT i.task FROM Issue i")
    Page<Task> findDistinctOnTask(PageRequest pageRequest);
}

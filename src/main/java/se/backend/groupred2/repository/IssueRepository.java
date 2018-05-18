package se.backend.groupred2.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {

    @Query("SELECT DISTINCT i.task FROM Issue i")
    Page<Task> findDistinctOnTask(Pageable pageable);
}

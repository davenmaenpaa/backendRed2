package se.backend.groupred2.service;

import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task;
import se.backend.groupred2.model.TaskStatus;
import se.backend.groupred2.repository.IssueRepository;
import se.backend.groupred2.repository.TaskRepository;
import se.backend.groupred2.service.exceptions.InvalidInputException;
import se.backend.groupred2.service.exceptions.InvalidTaskException;

import java.util.List;
import java.util.Optional;

@Service
public final class IssueService {
    private final IssueRepository issueRepository;
    private final TaskRepository taskRepository;

    public IssueService(IssueRepository issueRepository, TaskRepository taskRepository) {
        this.issueRepository = issueRepository;
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasksWithIssues(int page, int limit) {
        System.out.println("got into method");

        Page<Task> pagedTasks = issueRepository.findDistinctOnTask(PageRequest.of(page, limit));

        return pagedTasks.getContent();
    }

    public Issue createIssue(Long taskid, Issue issue) {
        Optional<Task> taskResult = taskRepository.findById(taskid);

        return taskResult.map(t -> {
            Task task = taskResult.get();

            validate(task);

            t.setStatus(TaskStatus.UNSTARTED);
            taskRepository.save(task);

            issue.setTask(task);

            return issueRepository.save(issue);
        }).orElseThrow(() -> new InvalidTaskException("Task doesn't exist"));
    }

    public Optional<Issue> update(Long issueId, Issue issue) {
        Optional<Issue> result = issueRepository.findById(issueId);

        return result.map(i -> {
            if (issue.getTitle() != null&& issue.getDescription() != null) {
                i.setTitle(issue.getTitle());
                i.setDescription(issue.getDescription());

            } else if (issue.getTitle() != null) {
                i.setTitle(issue.getTitle());

            } else if (issue.getDescription() != (null)) {
                i.setDescription(issue.getDescription());

            }

            issueRepository.save(result.get());

            return result;
        }).orElseThrow(() -> new InvalidInputException("Issue does not exist."));
    }

    private void validate(Task task) {
        if (!task.getStatus().equals(TaskStatus.DONE))
            throw new InvalidTaskException("Task does not have status Done");
    }
}

package se.backend.groupred2.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Issue;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Task.TaskStatus;
import se.backend.groupred2.repository.IssueRepository;
import se.backend.groupred2.repository.TaskRepository;
import se.backend.groupred2.service.exceptions.*;

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

    public Iterable<Issue> getAllIssues(int page, int limit) {
        return issueRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

    public List<Task> getAllTasksWithIssues(int page, int limit) {
        List<Task> result = issueRepository.findDistinctOnTask(PageRequest.of(page, limit)).getContent();

        if (result.isEmpty())
            throw new NoContentException();

        return result;
    }

    public Issue createIssue(Long taskid, Issue issue) {
        Optional<Task> taskResult = taskRepository.findById(taskid);

        return taskResult.map(t -> {
            Task task = taskResult.get();
            validate(task);

            taskRepository.save(task);
            issue.setTask(task);

            return issueRepository.save(issue);
        }).orElseThrow(() -> new InvalidTaskException("Task doesn't exist"));
    }

    public Issue update(Long issueId, Issue issue) {
        Optional<Issue> result = issueRepository.findById(issueId);

        return result.map(i -> {
            if (issue.getTitle() != null && issue.getDescription() != null) {
                i.setTitle(issue.getTitle()).setDescription(issue.getDescription());

            } else if (issue.getTitle() != null) {
                i.setTitle(issue.getTitle());
            } else if (issue.getDescription() != (null)) {
                i.setDescription(issue.getDescription());
            } else {
                throw new BadRequestException();
            }

            issueRepository.save(result.get());

            return i;
        }).orElseThrow(NotFoundException::new);
    }

    private void validate(Task task) {
        if (!task.getStatus().equals(TaskStatus.DONE))
            throw new InvalidTaskException("Task does not have status DONE");
    }
}

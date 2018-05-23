package se.backend.groupred2.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Task.TaskStatus;
import se.backend.groupred2.model.Task.TaskStatusDate;
import se.backend.groupred2.model.User;
import se.backend.groupred2.repository.TaskRepository;
import se.backend.groupred2.repository.TaskStatusRepository;
import se.backend.groupred2.repository.UserRepository;
import se.backend.groupred2.service.exceptions.InvalidInputException;
import se.backend.groupred2.service.exceptions.InvalidTaskException;
import se.backend.groupred2.service.exceptions.NoContentException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskStatusRepository taskStatusRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskStatusRepository = taskStatusRepository;
    }

    public Task createTask(Task task) {
        validateTask(task);

        taskRepository.save(task);
        taskStatusRepository.save(new TaskStatusDate(task, LocalDate.now(), task.getStatus()));

        return serverSideEvent(task);
    }

    public List<Task> getAllTasks(int page, int limit) {
        List<Task> result = taskRepository.findAll(PageRequest.of(page, limit)).getContent();

        if (result.isEmpty())
            throw new NoContentException();

        return result;
    }

    public Optional<Task> getTask(Long id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> deleteTask(Long id) {
        Optional<Task> result = taskRepository.findById(id);
        result.ifPresent(task -> taskRepository.deleteById(id));

        return result;
    }

    public Optional<Task> updateStatus(Long id, Task task) {
        Optional<Task> taskResult = taskRepository.findById(id);

        if (taskResult.isPresent()) {

            Task updatedTask = taskResult.get();
            updatedTask.setStatus(task.getStatus());

            taskStatusRepository.save(new TaskStatusDate(updatedTask, LocalDate.now(), updatedTask.getStatus()));
            taskRepository.save(updatedTask);

        } else {
            throw new InvalidTaskException("Could not find that task");
        }

        return taskResult;
    }

    public Optional<Task> addUserToTask(Long id, Long userId) {
        Optional<Task> taskResult = taskRepository.findById(id);
        Optional<User> userResult = userRepository.findById(userId);

        List<Task> taskItems = taskRepository.findAllTaskByUserId(userId);

        if (taskResult.isPresent() && userResult.isPresent()) {
            if (!userResult.get().isActive()) {
                throw new InvalidTaskException("That user is not active");

            } else if (taskItems.size() > 4) {
                throw new InvalidTaskException("To many tasks for that user, Max = 5");
            }

            Task temp = taskResult.get();
            temp.setUser(userResult.get());
            taskRepository.save(temp);

            return taskResult;
        } else {
            throw new InvalidTaskException("Could not find a user or task");
        }
    }

    public List<Task> getAllTasksByStatus(String status, int page, int limit) {
        validateStatus(status);

        List<Task> tasks = taskRepository.findAllByStatus(TaskStatus.valueOf(status), PageRequest.of(page, limit)).getContent();

        if (tasks.isEmpty()) {
            throw new InvalidTaskException("Could not find any tasks with that status");
        }

        return tasks;
    }

    public List<Task> getAllTasksByUserId(Long userId, int page, int limit) {
        List<Task> tasks = taskRepository.findAllTaskByUserId(userId, PageRequest.of(page, limit)).getContent();

        if (tasks.isEmpty()) {
            throw new InvalidTaskException("Could not find any tasks for that user");
        }

        return tasks;
    }

    public List<Task> getAllTasksByDescription(String description, int page, int limit) {
        List<Task> tasks = taskRepository.findAll(PageRequest.of(page, limit)).getContent();

        tasks = tasks.stream()
                .filter(task -> task.getDescription().contains(description))
                .collect(Collectors.toList());

        if (tasks.isEmpty()) {
            throw new InvalidTaskException("Could not find any tasks with that description");
        }

        return tasks;
    }

    public List<Task> getAllTasksByTeamId(Long teamId) {
        List<User> userResult = userRepository.findUsersByTeamId(teamId);

        if (userResult.isEmpty())
            throw new InvalidTaskException("Could not find any tasks for that team");

        List<Task> allTasks = new ArrayList<>();
        userResult.forEach(user -> allTasks.addAll(taskRepository.findAllTaskByUserId(user.getId())));

        return allTasks;
    }

    private void validateTask(Task task) {
        if (task.getTitle() == null || task.getTitle().isEmpty() || task.getDescription() == null || task.getStatus() == null)
            throw new InvalidInputException();
    }

    private void validateStatus(String status) {
        if (!status.equals("UNSTARTED") && !status.equals("STARTED") && !status.equals("DONE"))
            throw new InvalidTaskException("Incorrect status, have to be UNSTARTED, STARTED or DONE");
    }

    private Task serverSideEvent(Task task) {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target("http://localhost:8080").path("tasks/events");

        target.request(MediaType.TEXT_PLAIN_TYPE)
                .post(Entity.entity(task.toString(), MediaType.TEXT_PLAIN));

        return task;
    }
}
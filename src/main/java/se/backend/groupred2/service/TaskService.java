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
import se.backend.groupred2.service.exceptions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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

        return task;
    }

    public Task partialUpdate(Long id, HashMap hashMap) {
        Optional<Task> taskResult = taskRepository.findById(id);

        return taskResult.map(task -> {
            if (hashMap.isEmpty()) {
                throw new BadRequestException();

            } else if (hashMap.containsKey("description")) {
                task.setDescription(hashMap.get("description").toString());

            } else if (hashMap.containsKey("title")) {
                task.setTitle(hashMap.get("title").toString());

            } else if (hashMap.containsKey("status")) {
                String status = hashMap.get("status").toString();
                task.setStatus(validateStatus(status));

                taskStatusRepository.save(new TaskStatusDate(task, LocalDate.now(), task.getStatus()));

            } else if (hashMap.containsKey("user")) {
                long userId = Long.valueOf(hashMap.get("user").toString());
                Optional<User> userResult = userRepository.findById(userId);

                userResult.map(task::setUser).orElseThrow(() -> new InvalidUserException("User does not exist"));
            }

            return taskRepository.save(task);
        }).orElseThrow(BadRequestException::new);
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

    private TaskStatus validateStatus(String status) {
        if (!status.equals("UNSTARTED") && !status.equals("STARTED") && !status.equals("DONE"))
            throw new InvalidTaskException("Incorrect status, have to be UNSTARTED, STARTED or DONE");

        return TaskStatus.valueOf(status);
    }
}
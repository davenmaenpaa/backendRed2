package se.backend.groupred2.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import se.backend.groupred2.model.Task.Task;
import se.backend.groupred2.model.Task.TaskStatus;
import se.backend.groupred2.model.User;
import se.backend.groupred2.repository.TaskRepository;
import se.backend.groupred2.repository.UserRepository;
import se.backend.groupred2.service.exceptions.InvalidUserException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public final class UserService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    public User createUser(User user) {
        validate(user);

        return userRepository.save(new User(user.getFirstName(), user.getLastName(),
                user.getUserName(), user.isActive(), user.getUserNumber()));
    }

    public Iterable<User> getAllUsers(int page, int limit) {
        return userRepository.findAll(PageRequest.of(page, limit)).getContent();
    }

    public Optional<User> update(User user) {
        validate(user);
        Optional<User> result = userRepository.findById(user.getId());

        result.ifPresent(t -> {
            t.setFirstName(user.getFirstName());
            t.setLastName(user.getLastName());
            t.setUserName(user.getUserName());
            t.setUserNumber(user.getUserNumber());
            t.setActive(user.isActive());
            userRepository.save(result.get());
        });

        return result;
    }

    public Optional<User> deActivate(User user) {
        Optional<User> result = userRepository.findById(user.getId());

        result.ifPresent(t -> {
            t.deActivate();
            List<Task> tasks = getAllTasksByUserId(result.get().getId());

            tasks.forEach(task -> {
                task.setStatus(TaskStatus.UNSTARTED);
                taskRepository.save(task);
            });

            userRepository.save(result.get());
        });

        return result;
    }

    private List<Task> getAllTasksByUserId(Long userkId) {
        return taskRepository.findAllByUser_Id(userkId);
    }

    public List<User> getUserByUserNamefirstNameLastName(Long userNumber, String userName, String firstName, String lastName) {

        if (userNumber == 0) {
            if (!userName.equals("0")) {
                return userRepository.findUserByUserName(userName);

            } else if ((!firstName.equals("0"))) {
                return userRepository.findUserByFirstName(firstName);
            } else if (!lastName.equals("0")) {

                return userRepository.findUserByLastName(lastName);
            }
            
        } else if ((userNumber != 0)) {
            return userRepository.findByUserNumber(userNumber);

        } else {
            throw new InvalidUserException("fel");
        }

        return null;
    }

    public List<User> getALLUserByteamId(Long id) {

        List<User> user = userRepository.findUsersByTeamId(id);
        if (user.isEmpty())
            throw new InvalidUserException("den e tom");

        return userRepository.findAll().stream()  //gÃ¶r om detta till en strÃ¶m
                .filter(t -> t.getTeam().getId().equals(id)) //behÃ¥ll alla teams med det hÃ¤r idt
                .collect(Collectors.toList()); //gÃ¶r om strÃ¶mmen till en lista
    }

    private void validate(User user) {
        int UserName = user.getUserName().length();
        if (UserName < 10) {
            throw new InvalidUserException("UserName är minst än 10 token");
        } else if (user.getUserName().isEmpty() && user.getUserName() == null) {
            throw new InvalidUserException("userName is Empty");

        }
    }

}



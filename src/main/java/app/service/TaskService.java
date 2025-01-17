package app.service;

import app.entity.TaskEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Permission;
import app.entity.userattributes.Role;
import app.mapper.TaskMapper;
import app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.REPEATABLE_READ)
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskMapper taskMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TaskEntity get(UserDetails currentUser, UUID taskId) {
        UserEntity userEntity = userService.getUser(currentUser);

        if (!userService.hasUserRequiredPermissions(userEntity, List.of(Permission.WATCH_TASK))) {
            throw new AccessDeniedException("Access deny");
        }

        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

        return foundTask;
    }

    public UUID create(UserDetails currentUser, TaskEntity taskDetails) {
        if (!userService.hasUserRequiredPermissions(userService.getUser(currentUser), List.of(Permission.CREATE_TASK))) {
            throw new AccessDeniedException("Access deny");
        }

        if (!taskDetails.getAuthorEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Access deny");
        }

        taskDetails.setId(null);
        taskDetails.setComments(null);
        return taskRepository.save(taskDetails).getId();
    }

    public TaskEntity update(
            UserDetails currentUser,
            UUID taskId,
            TaskEntity updatedTask
    ) {
        UserEntity userEntity = userService.getUser(currentUser);

        if (!userService.hasUserRequiredPermissions(userEntity, List.of(Permission.UPDATE_TASK))) {
            throw new AccessDeniedException("Access deny");
        }

        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

        if (userEntity.getRole() == Role.ADMIN && !userEntity.getEmail().equals(foundTask.getAuthorEmail())) {
            throw new AccessDeniedException("Access deny");
        }

        foundTask = taskMapper.mapTasks(updatedTask, userEntity.getRole());
        foundTask.setId(taskId);

        return taskRepository.save(foundTask);
    }
    public void delete(
            UserDetails userDetails,
            UUID taskId
    ) {
        if (!userService.hasUserRequiredPermissions(userService.getUser(userDetails), List.of(Permission.DELETE_TASK))) {
            throw new IllegalArgumentException("User doesn't have permission for this operation");
        }

        TaskEntity foundTask = getTask(taskId);
        taskRepository.delete(foundTask);
    }

    private void checkTaskAccess(UserEntity userEntity, TaskEntity foundTask) {
        if (userEntity.getRole() != Role.ADMIN && !userEntity.getEmail().equals(foundTask.getExecutorEmail())) {
            throw new AccessDeniedException("Access deny");
        }
    }

    private TaskEntity getTask(UUID id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("task didn't find"));
    }
}

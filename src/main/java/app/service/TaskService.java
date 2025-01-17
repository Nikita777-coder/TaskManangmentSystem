package app.service;

import app.entity.TaskEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Permission;
import app.entity.userattributes.Role;
import app.mapper.TaskMapper;
import app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
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
            throw new IllegalArgumentException("User doesn't have permission for this operation");
        }

        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

        return foundTask;
    }

    public UUID create(UserDetails currentUser, TaskEntity taskDetails) {
        if (!userService.hasUserRequiredPermissions(userService.getUser(currentUser), List.of(Permission.CREATE_TASK))) {
            throw new IllegalArgumentException("User doesn't have permission for this operation");
        }

        taskDetails.setId(null);
        return taskRepository.save(taskDetails).getId();
    }

    // чекнуть, что у юзера есть права на изменение статусов, добавление комментов и т д при мапинге
    public TaskEntity update(
            UserDetails currentUser,
            UUID taskId,
            TaskEntity updatedTask
    ) {
        UserEntity userEntity = userService.getUser(currentUser);

        if (!userService.hasUserRequiredPermissions(userEntity, List.of(Permission.UPDATE_TASK))) {
            throw new IllegalArgumentException("User doesn't have permission for this operation");
        }

        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

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
        if (!userEntity.getEmail().equals(foundTask.getExecutorEmail()) || userEntity.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("Access deny");
        }
    }

    private TaskEntity getTask(UUID id) {
        return taskRepository.findById(id).
                orElseThrow(() -> new IllegalArgumentException("task didn't find"));
    }
}

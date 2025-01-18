package app.service;

import app.entity.TaskEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Permission;
import app.entity.userattributes.Role;
import app.mapper.TaskMapper;
import app.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
        checkUserPermissions(userEntity, List.of(Permission.WATCH_TASK));
        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

        return foundTask;
    }

    public UUID create(UserDetails currentUser, TaskEntity taskDetails) {
        checkUserPermissions(userService.getUser(currentUser), List.of(Permission.CREATE_TASK));
        if (!taskDetails.getAuthorEmail().equals(currentUser.getUsername())) {
            throw new AccessDeniedException("Access deny");
        }

        taskDetails.setId(null);
        taskDetails.setComments(null);
        checkExecutor(taskDetails);

        return taskRepository.save(taskDetails).getId();
    }

    public TaskEntity update(
            UserDetails currentUser,
            UUID taskId,
            TaskEntity updatedTask
    ) {
        UserEntity userEntity = userService.getUser(currentUser);
        checkUserPermissions(userEntity, List.of(Permission.UPDATE_TASK));
        TaskEntity foundTask = getTask(taskId);
        checkTaskAccess(userEntity, foundTask);

        if (userEntity.getRole() == Role.ADMIN && !userEntity.getEmail().equals(foundTask.getAuthorEmail())) {
            throw new AccessDeniedException("Access deny");
        }

        foundTask = taskMapper.mapTasks(updatedTask, userEntity.getRole());
        if (userEntity.getRole() == Role.ADMIN) checkExecutor(updatedTask);

        foundTask.setId(taskId);

        return taskRepository.save(foundTask);
    }
    public void delete(
            UserDetails userDetails,
            UUID taskId
    ) {
        checkUserPermissions(userService.getUser(userDetails), List.of(Permission.DELETE_TASK));
        TaskEntity foundTask = getTask(taskId);

        taskRepository.delete(foundTask);
    }
    public Page<TaskEntity> getAuthorTasks(UserDetails userDetails,
                                           String authorEmail,
                                           Pageable pageable) {
        UserEntity userEntity = userService.getUser(userDetails);

        checkUserPermissions(userEntity, List.of(Permission.WATCH_TASK));

        if (userEntity.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("AccessDeny");
        }

        Page<TaskEntity> tasks = taskRepository.findAllByAuthorEmail(authorEmail, pageable);

        return new PageImpl<>(tasks.getContent(), pageable, tasks.getTotalElements());
    }

    public Page<TaskEntity> getExecutorTasks(UserDetails userDetails,
                                             String executorEmail,
                                             Pageable pageable) {
        UserEntity userEntity = userService.getUser(userDetails);

        checkUserPermissions(userEntity, List.of(Permission.WATCH_TASK));

        if (userEntity.getRole() != Role.ADMIN || !userDetails.getUsername().equals(executorEmail)) {
            throw new AccessDeniedException("AccessDeny");
        }

        Page<TaskEntity> tasks = taskRepository.findAllByExecutorEmail(executorEmail, pageable);

        return new PageImpl<>(tasks.getContent(), pageable, tasks.getTotalElements());
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
    private void checkUserPermissions(UserEntity userEntity, List<Permission> permissions) {
        if (!userService.hasUserRequiredPermissions(userEntity, permissions)) {
            throw new AccessDeniedException("Access deny");
        }
    }
    private void checkExecutor(TaskEntity taskEntity) {
        if (taskEntity.getExecutorEmail() != null) {
            userService.getUser(taskEntity.getExecutorEmail());
        }
    }
}

package app.controller;

import app.entity.TaskEntity;
import app.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public TaskEntity getTask(@AuthenticationPrincipal UserDetails currentUserDetails,
                        @RequestParam("task-id") UUID taskId) {
        return taskService.get(currentUserDetails, taskId);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UUID createTask(@AuthenticationPrincipal UserDetails currentUserDetails,
                           @RequestBody TaskEntity taskDetails) {
        return taskService.create(currentUserDetails, taskDetails);
    }

    @PatchMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public TaskEntity updateTask(@AuthenticationPrincipal UserDetails currentUserDetails,
                                 @RequestParam("task-id") UUID id,
                                 @RequestBody TaskEntity updatedDetails) {
        return taskService.update(
                    currentUserDetails,
                    id,
                    updatedDetails
                );
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@AuthenticationPrincipal UserDetails currentUserDetails,
                                 @RequestParam("task-id") UUID id) {
        taskService.delete(currentUserDetails, id);
    }

    @GetMapping("/author")
    @ResponseStatus(HttpStatus.OK)
    public Page<TaskEntity> getTaskOfAuthor(@AuthenticationPrincipal UserDetails currentUserDetails,
                                            @RequestParam("author-email") String authorEmail,
                                            @ParameterObject Pageable pageable) {
        return taskService.getAuthorTasks(currentUserDetails, authorEmail, pageable);
    }

    @GetMapping("/executor")
    @ResponseStatus(HttpStatus.OK)
    public Page<TaskEntity> getTaskOfExecutor(@AuthenticationPrincipal UserDetails currentUserDetails,
                                            @RequestParam("executor-email") String executorEmail,
                                              @ParameterObject Pageable pageable) {
        return taskService.getExecutorTasks(currentUserDetails, executorEmail, pageable);
    }
}

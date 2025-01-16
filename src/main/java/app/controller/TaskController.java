package app.controller;

import app.entity.TaskEntity;
import app.service.TaskService;
import lombok.RequiredArgsConstructor;
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

    //
}

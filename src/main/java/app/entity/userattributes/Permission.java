package app.entity.userattributes;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
    CREATE_TASK("create-task"),
    UPDATE_TASK("update-task"),
    WATCH_TASK("watch-task"),
    DELETE_TASK("delete-task"),
    CHANGE_STATUS_TASK("change-status-task"),
    CHANGE_PRIORITY_TASK("change-priority-task"),
    UPDATE_EXECUTORS_TASK("update-executors-task"),
    MAKE_COMMENTS_TASK("make-comments-task");

    private final String permission;
}


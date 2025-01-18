package app.service;

import app.dto.comment.Comment;
import app.entity.CommentEntity;
import app.entity.TaskEntity;
import app.entity.UserEntity;
import app.entity.userattributes.Permission;
import app.entity.userattributes.Role;
import app.mapper.CommentMapper;
import app.repository.CommentRepository;
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
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;
    public CommentEntity get(UserDetails userDetails,
                             UUID commentId) {
        checkUserPermissions(userService.getUser(userDetails), List.of(Permission.MAKE_COMMENTS_TASK));

        return getComment(commentId);
    }
    public UUID add(UserDetails userDetails,
                    Comment newComment) {
        UserEntity userEntity = userService.getUser(userDetails);
        checkUserPermissions(userEntity, List.of(Permission.MAKE_COMMENTS_TASK));
        TaskEntity taskEntity = getTask(newComment.getTaskId());
        checkUserPermissionToAddComments(taskEntity, userEntity);

        return commentRepository.save(commentMapper.commentToCommentEntity(newComment, userDetails)).getId();
    }
    public CommentEntity update(UserDetails userDetails,
                                Comment updatedComment) {
        UserEntity userEntity = userService.getUser(userDetails);
        checkUserPermissions(userEntity, List.of(Permission.MAKE_COMMENTS_TASK));
        TaskEntity taskEntity = getTask(updatedComment.getTaskId());
        checkUserPermissionToAddComments(taskEntity, userEntity);

        return commentRepository.save(commentMapper.commentToCommentEntity(updatedComment, userDetails));
    }
    public void delete(UserDetails userDetails,
                       UUID commentId) {
        UserEntity userEntity = userService.getUser(userDetails);
        checkUserPermissions(userEntity, List.of(Permission.MAKE_COMMENTS_TASK));
        CommentEntity commentEntity = getComment(commentId);

        commentRepository.delete(commentEntity);
    }
    private void checkUserPermissions(UserEntity userEntity, List<Permission> permissions) {
        if (!userService.hasUserRequiredPermissions(userEntity, permissions)) {
            throw new AccessDeniedException("Access deny");
        }
    }
    private TaskEntity getTask(UUID taskId) {
        return taskRepository.findById(taskId).orElseThrow(() -> new IllegalArgumentException("task not found"));
    }
    private void checkUserPermissionToAddComments(TaskEntity taskEntity, UserEntity userEntity) {
        if (userEntity.getRole() != Role.ADMIN && !taskEntity.getExecutorEmail().equals(userEntity.getEmail())) {
            throw new AccessDeniedException("Access deny");
        }
    }
    private CommentEntity getComment(UUID id) {
        return commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("comment not found"));
    }
}

package app.mapper;

import app.dto.comment.Comment;
import app.dto.comment.UpdateComment;
import app.entity.CommentEntity;
import app.entity.TaskEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring"
)

public interface CommentMapper {
    @Mapping(target = "id", ignore = true)
    CommentEntity commentToCommentEntity(Comment comment, UserDetails userDetails, TaskEntity taskEntity);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comment", ignore = true)
    @Mapping(target = "userEmail", ignore = true)
    CommentEntity commentToCommentEntity(UpdateComment comment);
    
    @AfterMapping
    default void fufilleArguments(Comment comment, UserDetails userDetails, TaskEntity taskEntity, @MappingTarget CommentEntity commentEntity) {
        commentEntity.setCommentTimeCreation(LocalDateTime.now());
        commentEntity.setUserEmail(userDetails.getUsername());
        commentEntity.setTaskEntity(taskEntity);
    }

    @AfterMapping
    default void fufilleArguments(UpdateComment comment, @MappingTarget CommentEntity commentEntity) {
        commentEntity.setCommentTimeCreation(LocalDateTime.now());
        commentEntity.setComment(comment.getComment().getComment());
    }
}

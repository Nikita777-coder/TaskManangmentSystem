package app.mapper;

import app.dto.comment.Comment;
import app.entity.CommentEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

@Mapper(
        componentModel = "spring"
)

public interface CommentMapper {
    CommentEntity commentToCommentEntity(Comment comment, UserDetails userDetails);
    
    @AfterMapping
    default void fufilleArguments(Comment comment, UserDetails userDetails, @MappingTarget CommentEntity commentEntity) {
        commentEntity.setCommentTimeCreation(LocalDateTime.now());
        commentEntity.setUserEmail(userDetails.getUsername());
    }
}

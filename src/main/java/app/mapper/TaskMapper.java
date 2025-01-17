package app.mapper;

import app.entity.TaskEntity;
import app.entity.userattributes.Role;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.security.access.AccessDeniedException;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorEmail", ignore = true)
    TaskEntity mapTasks(TaskEntity partiallyUpdatedEntity, Role userRole);
    @BeforeMapping
    default void checkUpdateEntityOnPermissions(TaskEntity partiallyUpdatedEntity, Role userRole) {
        if ((userRole == Role.USER &&
                (partiallyUpdatedEntity.getDescription() != null ||
                        partiallyUpdatedEntity.getTaskPriority() != null ||
                        partiallyUpdatedEntity.getExecutorEmail() != null ||
                        partiallyUpdatedEntity.getHeader() != null||
                        partiallyUpdatedEntity.getAuthorEmail() != null))
                || partiallyUpdatedEntity.getComments() != null) {
            throw new AccessDeniedException("Access deny");
        }
    }
}

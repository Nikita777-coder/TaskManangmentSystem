package app.mapper;

import app.entity.TaskEntity;
import app.entity.userattributes.Role;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    TaskEntity mapTasks(TaskEntity partiallyUpdatedEntity, Role userRole);
    @BeforeMapping
    default void checkUpdateEntityOnPermissions(TaskEntity partiallyUpdatedEntity, Role userRole) {
        if (userRole == Role.USER && (
                partiallyUpdatedEntity.getTaskPriority() != null ||
                partiallyUpdatedEntity.getExecutorId() != null
            )
        ) {
            throw new IllegalArgumentException("User deny change some properties, don't have access");
        }
    }
}

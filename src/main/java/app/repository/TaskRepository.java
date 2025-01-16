package app.repository;

import app.entity.TaskEntity;
import app.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    Optional<TaskEntity> findById(UUID id);
}

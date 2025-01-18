package app.repository;

import app.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {}

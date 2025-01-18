package app.repository;

import app.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    Page<TaskEntity> findAllByAuthorEmail(String authorEmail, Pageable pageable);
    Page<TaskEntity> findAllByExecutorEmail(String executorEmail, Pageable pageable);
}

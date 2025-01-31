package app.entity;

import app.entity.taskattributes.TaskPriority;
import app.entity.taskattributes.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="tasks")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String header;

    @Column
    private String description;

    @Column(name = "task_status")
    private TaskStatus taskStatus;

    @Column(name = "task_priority")
    private TaskPriority taskPriority;

    @Column(name = "author_email")
    private String authorEmail;

    @Column(name = "executor_email")
    private String executorEmail;

    @OneToMany(
            mappedBy = "taskEntity",
            cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private List<CommentEntity> comments;
}

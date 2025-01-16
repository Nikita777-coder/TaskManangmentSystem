package app.entity;

import app.entity.taskattributes.TaskPriority;
import app.entity.taskattributes.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name="tasks")
@Getter
@Setter
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

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "executor_id")
    private UUID executorId;

    @OneToMany(mappedBy = "comments_for_tasks")
    private List<CommentEntity> comments;
}

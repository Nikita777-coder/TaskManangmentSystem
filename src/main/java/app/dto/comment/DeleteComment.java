package app.dto.comment;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DeleteComment {
    @NotNull(message = "can't be null")
    private UUID taskId;

    @NotNull(message = "can't be null")
    private UUID commentId;
}

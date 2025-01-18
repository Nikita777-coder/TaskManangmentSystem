package app.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class Comment {
    @NotNull(message = "can't be null")
    private UUID taskId;

    @NotBlank(message = "can't be empty")
    private String comment;
}

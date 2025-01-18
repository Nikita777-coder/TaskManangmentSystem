package app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonEmail {
    @NotBlank(message = "can't be empty")
    private String email;
}

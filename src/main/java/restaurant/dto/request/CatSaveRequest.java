package restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CatSaveRequest(
        @NotBlank(message = "the name must be unique")
        String name
) {
}

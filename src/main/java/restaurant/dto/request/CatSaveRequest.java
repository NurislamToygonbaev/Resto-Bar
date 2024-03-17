package restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CatSaveRequest(
        @NotBlank
        String name
) {
}

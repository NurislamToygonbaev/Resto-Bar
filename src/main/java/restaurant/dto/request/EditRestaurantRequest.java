package restaurant.dto.request;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import restaurant.entities.enums.RestType;

public record EditRestaurantRequest(
        @NotBlank
        String name,
        @NotBlank
        String location,
        @Enumerated(EnumType.STRING)
        RestType restType,
        @NotBlank
        int service
) {
}

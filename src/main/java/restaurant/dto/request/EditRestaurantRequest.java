package restaurant.dto.request;

import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import restaurant.entities.enums.RestType;
import restaurant.validation.experience.ExperienceValidation;

public record EditRestaurantRequest(
        String name,
        String location,
        RestType restType,
        @ExperienceValidation
        @NotNull
        Integer service
) {
}

package restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import restaurant.validation.experience.ExperienceValidation;

import java.math.BigDecimal;

public record SaveMenuRequest(
        @NotBlank
        String name,
        @NotBlank
        String image,
        @ExperienceValidation
        @NotNull
        Integer price,
        @NotBlank
        String description,
        boolean isVegetarian,
        @ExperienceValidation
        Integer quantity
) {
}

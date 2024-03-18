package restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import restaurant.validation.experience.ExperienceValidation;

import java.math.BigDecimal;

public record SaveMenuRequest(
        @NotBlank
        String name,
        @NotBlank
        String image,
        BigDecimal price,
        @NotBlank
        String description,
        boolean isVegetarian,
        @ExperienceValidation
        int quantity
) {
}

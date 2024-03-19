package restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import restaurant.validation.experience.ExperienceValidation;

import java.math.BigDecimal;

public record SaveMenuRequest(
        @NotBlank(message = "the name must be unique")
        String name,
        @NotBlank(message = "the image should not be empty")
        String image,
        @ExperienceValidation
        @NotNull
        Integer price,
        @NotBlank(message = "the description should not be empty")
        String description,
        boolean isVegetarian,
        @ExperienceValidation
        Integer quantity
) {
}

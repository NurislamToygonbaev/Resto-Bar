package restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import restaurant.validation.experience.ExperienceValidation;

import java.math.BigDecimal;

@Builder
public record ChequeUpdateRequest(
        @ExperienceValidation
        @NotNull
        Integer priceAvg
) {
}

package restaurant.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record SignResponse(
        Long id,
        String token,
        String email,
        @Enumerated(EnumType.STRING)
        String role,
        HttpStatus httpStatus,
        String message
) {
}

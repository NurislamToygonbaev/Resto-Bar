package restaurant.dto.response;

import lombok.Builder;
import org.springframework.http.HttpStatus;

@Builder
public record SignResponse(
        Long id,
        String token,
        String email,
        String role,
        HttpStatus httpStatus,
        String message
) {
}

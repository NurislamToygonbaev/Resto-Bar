package restaurant.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record StopListResponse(
        String name,
        String reason,
        LocalDate date
) {
}

package restaurant.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import restaurant.entities.enums.RestType;

public record ALlRestResponse(
        Long id,
        String name,
        String location,
        @Enumerated(EnumType.STRING)
        RestType restType,
        int numberOfEmployees,
        String service
) {
}

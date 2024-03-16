package restaurant.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import restaurant.entities.enums.Role;

import java.time.LocalDate;

@Builder
public record AllUsersResponse(
        Long id,
        String lastName,
        String firstName,
        LocalDate dateOfBirth,
        String email,
        String password,
        String phoneNumber,
        @Enumerated(EnumType.STRING)
        Role role,
        int experience
) {
}

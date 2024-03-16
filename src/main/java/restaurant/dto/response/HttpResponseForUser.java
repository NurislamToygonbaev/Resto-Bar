package restaurant.dto.response;

import lombok.Builder;
import restaurant.entities.enums.Role;

@Builder
public record HttpResponseForUser(
        SimpleResponse simpleResponse,
        String token,
        String email,
        Role role
) {
}

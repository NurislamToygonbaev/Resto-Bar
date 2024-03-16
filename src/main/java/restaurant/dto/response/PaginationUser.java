package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record PaginationUser(
        int page,
        int size,
        List<AllUsersResponse> allUsersResponses
) {
}

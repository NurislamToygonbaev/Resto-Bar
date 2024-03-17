package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record StopListPagination(
        int page,
        int size,
        List<StopListResponse> responses
) {
}

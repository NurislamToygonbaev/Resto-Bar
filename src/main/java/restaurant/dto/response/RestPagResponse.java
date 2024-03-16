package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record RestPagResponse(
        int page,
        int size,
        List<ALlRestResponse> responses
) {
}

package restaurant.dto.request;

import lombok.Builder;

@Builder
public record CatSaveRequest(
        String name
) {
}

package restaurant.dto.response;

import lombok.Builder;

@Builder
public record CategoriesResponse(
        Long id,
        String name
) {

}

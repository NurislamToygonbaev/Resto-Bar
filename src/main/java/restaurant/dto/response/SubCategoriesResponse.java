package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SubCategoriesResponse(
        Long id,
        String name,
        List<MenuItemsResponse> responses
) {
}

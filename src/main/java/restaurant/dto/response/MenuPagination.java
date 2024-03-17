package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MenuPagination(
        int page,
        int size,
        List<MenuItemsResponse> response
) {
}

package restaurant.dto.response;

import lombok.Builder;
import restaurant.entities.MenuItem;

import java.util.List;

@Builder
public record CatResponse(
        Long id,
        String name,
        List<String> subCatName,
        List<MenuItemsResponse> menuItems
) {
}

package restaurant.dto.response;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MenuItemsResponse(
        Long id,
        String name,
        String image,
        BigDecimal price,
        String description,
        boolean isVegetarian,
        int quantity
) {
}

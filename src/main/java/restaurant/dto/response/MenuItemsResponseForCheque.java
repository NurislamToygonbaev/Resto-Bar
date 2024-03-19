package restaurant.dto.response;

import lombok.Builder;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
public record MenuItemsResponseForCheque(
        String name,
        String image,
        String price,
        String description,
        boolean isVegetarian
) {


}

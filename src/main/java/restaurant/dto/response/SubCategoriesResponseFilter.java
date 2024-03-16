package restaurant.dto.response;

import java.util.List;

public record SubCategoriesResponseFilter(
        String name,
        List<CategoriesResponse> responses
) {
}

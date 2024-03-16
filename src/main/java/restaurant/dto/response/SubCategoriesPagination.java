package restaurant.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record SubCategoriesPagination(
        int page,
        int size,
        List<CategoriesResponse> responses
) {
}

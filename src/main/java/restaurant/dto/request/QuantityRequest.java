package restaurant.dto.request;

import lombok.Builder;

@Builder
public record QuantityRequest(
        int quantity
) {
}

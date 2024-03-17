package restaurant.dto.request;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ChequeUpdateRequest(
        BigDecimal priceAvg
) {
}

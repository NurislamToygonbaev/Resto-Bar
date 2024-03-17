package restaurant.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record ChequeResponses(
        String fullName,
        List<MenuItemsResponseForCheque> responses,
        BigDecimal priceAvg,
        String service,
        BigDecimal totalSum,
        LocalDate createdAt
) {
}

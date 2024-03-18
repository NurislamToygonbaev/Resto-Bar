package restaurant.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record ChequeResponses(
        String fullName,
        List<MenuItemsResponseForCheque> responses,
        String priceAvg,
        String service,
        String totalSum,
        LocalDate createdAt
) {
}

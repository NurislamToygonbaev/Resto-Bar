package restaurant.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record ChequeSaveRequest(
        List<Long> menuIds

) {
}

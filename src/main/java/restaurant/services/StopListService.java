package restaurant.services;

import restaurant.dto.response.StopListPagination;
import restaurant.dto.response.StopListResponse;

import java.security.Principal;

public interface StopListService {
    StopListPagination findAll(int page, int size, Principal principal);

    StopListResponse findById(Long stopId, Principal principal);
}

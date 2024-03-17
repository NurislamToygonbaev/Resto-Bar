package restaurant.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import restaurant.dto.response.StopListPagination;
import restaurant.dto.response.StopListResponse;
import restaurant.entities.Restaurant;
import restaurant.entities.StopList;
import restaurant.entities.User;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.StopListRepository;
import restaurant.services.StopListService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StopListServiceImpl implements StopListService {
    private final StopListRepository stopListRepo;
    private final CurrentUserService currentUserService;

    @Override
    public StopListPagination findAll(int page, int size, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Long resId = user.getRestaurant().getId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<StopList> stopLists = stopListRepo.findStopListsByResId(resId, pageable);

        if (stopLists.isEmpty()) throw new NotFoundException("Stop Lists not found");

        List<StopListResponse> collected = stopLists.getContent().stream()
                .map(this::convertToList)
                .collect(Collectors.toList());

        return StopListPagination.builder()
                .page(stopLists.getNumber() + 1)
                .size(stopLists.getTotalPages())
                .responses(collected)
                .build();
    }

    @Override
    public StopListResponse findById(Long stopId, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Restaurant adminRestaurant = user.getRestaurant();

        StopList list = stopListRepo.getStopListById(stopId);
        Restaurant userRestaurant = list.getMenuItem().getRestaurant();

        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        return StopListResponse.builder()
                .name(list.getMenuItem().getName())
                .reason(list.getReason())
                .date(list.getDate())
                .build();
    }

    private StopListResponse convertToList(StopList stopList) {
        return new StopListResponse(stopList.getMenuItem().getName(),
                stopList.getReason(), stopList.getDate());
    }
}

package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.MenuItem;
import restaurant.entities.Restaurant;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.services.MenuItemService;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepo;
    private final RestaurantRepository restaurantRepo;

    @Override @Transactional
    public SimpleResponse save(Long resId, SaveMenuRequest saveMenuRequest) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        MenuItem menuItem = new MenuItem();
        menuItem.setName(saveMenuRequest.name());
        menuItem.setImage(saveMenuRequest.image());
        menuItem.setPrice(saveMenuRequest.price());
        menuItem.setDescription(saveMenuRequest.description());
        menuItem.setVegetarian(saveMenuRequest.isVegetarian());

        restaurant.addMenuItem(menuItem);
        menuItemRepo.save(menuItem);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu successfully created")
                .build();
    }
}

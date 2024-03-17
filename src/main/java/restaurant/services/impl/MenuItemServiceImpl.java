package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.MenuItem;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.ForbiddenException;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.MenuItemService;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepo;
    private final CurrentUserService currentUserService;
    private final RestaurantRepository restaurantRepo;

    private void checkName(String name) {
        boolean b = menuItemRepo.existsByName(name);
        if (b) throw new AlreadyExistsException("Category with name: " + name + " already have");
    }
    private void checkMenuId(Long menuId){
        menuItemRepo.getMenuById(menuId);
    }

    @Override
    public SimpleResponse save(Principal principal, SaveMenuRequest saveMenuRequest) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF))){
            throw new ForbiddenException("Forbidden 403");
        }
        checkName(saveMenuRequest.name());
        Restaurant restaurant = user.getRestaurant();

        MenuItem menuItem = new MenuItem();
        menuItem.setName(saveMenuRequest.name());
        menuItem.setImage(saveMenuRequest.image());
        menuItem.setPrice(saveMenuRequest.price());
        menuItem.setDescription(saveMenuRequest.description());
        menuItem.setVegetarian(saveMenuRequest.isVegetarian());

        restaurant.addMenuItem(menuItem);
        menuItem.setRestaurant(restaurant);
        menuItemRepo.save(menuItem);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menuItem.getName()+" successfully created")
                .build();
    }

    @Override
    public MenuItemsResponse findById(Long menuId, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF) ||
                user.getRole().equals(Role.WAITER))){
            throw new ForbiddenException("Forbidden 403");
        }
        checkMenuId(menuId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestByMenuId(menuId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        MenuItem menuItem = menuItemRepo.getMenuById(menuId);

        return MenuItemsResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .image(menuItem.getImage())
                .price(menuItem.getPrice())
                .description(menuItem.getDescription())
                .isVegetarian(menuItem.isVegetarian())
                .build();
    }
}

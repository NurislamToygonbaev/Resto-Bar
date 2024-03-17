package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.MenuPagination;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.MenuItem;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.ForbiddenException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.MenuItemService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

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
    private User currentUser(Principal principal){
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF) ||
                user.getRole().equals(Role.WAITER))) {
            throw new ForbiddenException("Forbidden 403");
        }
        return user;
    }
    private User currentUserAdmin(Principal principal){
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF))) {
            throw new ForbiddenException("Forbidden 403");
        }
        return user;
    }

    @Override
    public SimpleResponse save(Principal principal, SaveMenuRequest saveMenuRequest) {
        checkName(saveMenuRequest.name());
        User user = currentUserAdmin(principal);
        MenuItem menuItem = new MenuItem();
        checkName(saveMenuRequest.name());
        Restaurant restaurant = user.getRestaurant();

        menuItem.setName(saveMenuRequest.name());
        menuItem.setImage(saveMenuRequest.image());
        menuItem.setPrice(saveMenuRequest.price());
        menuItem.setDescription(saveMenuRequest.description());
        menuItem.setVegetarian(saveMenuRequest.isVegetarian());
        menuItem.setQuantity(saveMenuRequest.quantity());

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
        User user = currentUser(principal);
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
                .quantity(menuItem.getQuantity())
                .build();
    }

    @Override
    public MenuPagination findAll(int page, int size, Principal principal) {
        User user = currentUser(principal);
        Long resId = user.getRestaurant().getId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<MenuItem> menuItems = menuItemRepo.findMenuByRestaurantId(resId, pageable);

        if (menuItems.isEmpty()) {
            throw new NotFoundException("Menu Items not found");
        }

        List<MenuItemsResponse> collected = menuItems.getContent().stream()
                .map(this::convertToMenu)
                .collect(Collectors.toList());

        return MenuPagination.builder()
                .page(menuItems.getNumber() + 1)
                .size(menuItems.getTotalPages())
                .response(collected)
                .build();
    }

    @Override
    public SimpleResponse updateMenu(Long menuId, SaveMenuRequest saveMenuRequest, Principal principal) {
        MenuItem menu = menuItemRepo.getMenuById(menuId);
        User user = currentUserAdmin(principal);

        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestByMenuId(menuId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        menu.setName(saveMenuRequest.name());
        menu.setImage(saveMenuRequest.image());
        menu.setDescription(saveMenuRequest.description());
        menu.setPrice(saveMenuRequest.price());
        menu.setVegetarian(saveMenuRequest.isVegetarian());
        menu.setQuantity(saveMenuRequest.quantity());
        menuItemRepo.save(menu);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menu.getName()+" Successfully updated")
                .build();
    }

    @Override
    public SimpleResponse deleteMenu(Long menuId, Principal principal) {

        User user = currentUserAdmin(principal);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestByMenuId(menuId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }
        MenuItem menu = menuItemRepo.getMenuById(menuId);
        menuItemRepo.delete(menu);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menu.getName()+" Successfully deleted")
                .build();
    }

    @Override
    public MenuPagination searchMenu(String keyword, Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        User user = currentUser(principal);
        Long resId = user.getRestaurant().getId();

        Page<MenuItem> menuItemPage = menuItemRepo.findMenuById(keyword, resId, pageable);

        if (menuItemPage.isEmpty()) throw new BedRequestException("Menu Items not found");

        List<MenuItemsResponse> collected = menuItemPage.getContent().stream()
                .map(this::convertToMenu)
                .collect(Collectors.toList());

        return MenuPagination.builder()
                .page(menuItemPage.getNumber() + 1)
                .size(menuItemPage.getTotalPages())
                .response(collected)
                .build();
    }

    @Override
    public MenuPagination filterByPrice(String ascOrDesc, int page, int size, Principal principal) {
        User currentUser = currentUser(principal);
        Pageable pageable = PageRequest.of(page - 1, size);
        Long resId = currentUser.getRestaurant().getId();

        Page<MenuItem> menuItemPage = menuItemRepo.filterByPrice(ascOrDesc, resId, pageable);

        if (menuItemPage.isEmpty()) throw new BedRequestException("Menu Items not found");

        List<MenuItemsResponse> collected = menuItemPage.getContent().stream()
                .map(this::convertToMenu)
                .collect(Collectors.toList());

        return MenuPagination.builder()
                .page(menuItemPage.getNumber() + 1)
                .size(menuItemPage.getTotalPages())
                .response(collected)
                .build();
    }

    @Override
    public MenuPagination filterVegetarian(boolean trueOrFalse, int page, int size, Principal principal) {
        User currentedUser = currentUser(principal);
        Long resId = currentedUser.getRestaurant().getId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<MenuItem> menuItemPage = menuItemRepo.filterByVegetarian(trueOrFalse, resId, pageable);
        if (menuItemPage.isEmpty()) throw new BedRequestException("Menu Items not found");

        List<MenuItemsResponse> collected = menuItemPage.getContent().stream()
                .map(this::convertToMenu)
                .collect(Collectors.toList());

        return MenuPagination.builder()
                .page(menuItemPage.getNumber() + 1)
                .size(menuItemPage.getTotalPages())
                .response(collected)
                .build();
    }

    private MenuItemsResponse convertToMenu(MenuItem menuItem) {
        return new MenuItemsResponse(
                menuItem.getId(), menuItem.getName(), menuItem.getImage(),
                menuItem.getPrice(), menuItem.getDescription(), menuItem.isVegetarian(),
                menuItem.getQuantity()
        );
    }
}

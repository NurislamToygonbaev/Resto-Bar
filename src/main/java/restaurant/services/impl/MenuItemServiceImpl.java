package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import restaurant.dto.request.QuantityRequest;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.MenuPagination;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.*;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.MenuItemRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.StopListRepository;
import restaurant.repository.SubCategoryRepository;
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
    private final StopListRepository stopListRepo;
    private final SubCategoryRepository subCategoryRepo;

    private void checkName(String name) {
        boolean b = menuItemRepo.existsByName(name);
        if (b) throw new AlreadyExistsException("Category with name: " + name + " already have");
    }
    private void checkMenuId(Long menuId){
        menuItemRepo.getMenuById(menuId);
    }
    @Override @Transactional
    public SimpleResponse save(Long subId, Principal principal, SaveMenuRequest saveMenuRequest) {
        SubCategory subCategory = subCategoryRepo.getSubCategoryId(subId);
        User user = currentUserService.adminAndChef(principal);
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
        menuItem.setSubCategory(subCategory);
        subCategory.addMenuItem(menuItem);

        menuItemRepo.save(menuItem);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menuItem.getName()+" successfully created")
                .build();
    }

    @Override
    public MenuItemsResponse findById(Long menuId, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        checkMenuId(menuId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant userRestaurant = restaurantRepo.getRestByMenuId(menuId);
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

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
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Long resId = user.getRestaurant().getId();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<MenuItem> menuItems = menuItemRepo.findMenuByRestaurantId(resId, pageable);

        if (menuItems.isEmpty()) throw new NotFoundException("Menu Items not found");

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
        User user = currentUserService.adminAndChef(principal);

        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant userRestaurant = restaurantRepo.getRestByMenuId(menuId);
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        menu.setName(saveMenuRequest.name());
        menu.setImage(saveMenuRequest.image());
        menu.setDescription(saveMenuRequest.description());
        menu.setPrice(saveMenuRequest.price());
        menu.setVegetarian(saveMenuRequest.isVegetarian());
        menu.setQuantity(saveMenuRequest.quantity());

        menuItemRepo.save(menu);

        StopList stopList = menu.getStopList();
        if (stopList != null) {
            stopListRepo.delete(stopList);
        }

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menu.getName()+" Successfully updated")
                .build();
    }

    @Override
    public SimpleResponse deleteMenu(Long menuId, Principal principal) {
        User user = currentUserService.adminAndChef(principal);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant userRestaurant = restaurantRepo.getRestByMenuId(menuId);
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        MenuItem menu = menuItemRepo.getMenuById(menuId);
        List<Cheque> cheques = menu.getCheques();
        for (Cheque cheque : cheques) {
            cheque.getMenuItems().remove(menu);
        }
        menuItemRepo.delete(menu);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Menu with name: "+menu.getName()+" Successfully deleted")
                .build();
    }

    @Override
    public MenuPagination searchMenu(String keyword, Principal principal, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        User user = currentUserService.adminAndChefAndWaiter(principal);
        Long resId = user.getRestaurant().getId();

        Page<MenuItem> menuItemPage = menuItemRepo.findMenuById("%"+keyword+"%", resId, pageable);

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
        User currentUser = currentUserService.adminAndChefAndWaiter(principal);
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
        User currentedUser = currentUserService.adminAndChefAndWaiter(principal);
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

    @Override
    public SimpleResponse addQuantity(Long menuId, QuantityRequest request, Principal principal) {
        User user = currentUserService.adminAndChef(principal);
        MenuItem menu = menuItemRepo.getMenuById(menuId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant userRestaurant = menu.getRestaurant();
        currentUserService.checkForbidden(adminRestaurant, userRestaurant);

        menu.setQuantity(request.quantity());
        menuItemRepo.save(menu);

        StopList stopList = menu.getStopList();
        if (stopList != null){
            stopListRepo.delete(stopList);
        }
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Successfully added quantity to menu with name: "+menu.getName())
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

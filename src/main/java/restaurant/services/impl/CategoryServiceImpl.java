package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.*;
import restaurant.entities.Category;
import restaurant.entities.Restaurant;
import restaurant.entities.SubCategory;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.ForbiddenException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.CategoryRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.CategoryService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepo;
    private final CurrentUserService currentUserService;
    private final RestaurantRepository restaurantRepo;

    private void checkName(String name) {
        boolean b = categoryRepo.existsByName(name);
        if (b) throw new AlreadyExistsException("Category with name: " + name + " already have");
    }
    private void checkCatId(Long catId){
        categoryRepo.getCatById(catId);
    }

    @Override
    public SimpleResponse saveCat(CatSaveRequest catSaveRequest, Principal principal) {
        checkName(catSaveRequest.name());
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF))) {
            throw new ForbiddenException("Forbidden 403");
        }
        Category save = categoryRepo.save(
                Category.builder()
                        .name(catSaveRequest.name())
                        .build()
        );
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category saved with name: " + save.getName())
                .build();
    }

    @Override
    public CatResponse findById(Long catId, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.CHEF) || user.getRole().equals(Role.ADMIN) ||
                user.getRole().equals(Role.WAITER))) {
            throw new ForbiddenException("Forbidden 403");
        }
        checkCatId(catId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestaurantByCatId(catId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        Category category = categoryRepo.getCatById(catId);

        List<String> subCatNames = category.getSubCategories().stream()
                .map(SubCategory::getName)
                .collect(Collectors.toList());

        List<MenuItemsResponse> menuItemsResponses = category.getSubCategories().stream()
                .flatMap(subCategory -> subCategory.getMenuItems().stream())
                .map(menuItem -> new MenuItemsResponse(
                        menuItem.getId(), menuItem.getName(), menuItem.getImage(),
                        menuItem.getPrice(), menuItem.getDescription(), menuItem.isVegetarian(),
                        menuItem.getQuantity()
                ))
                .collect(Collectors.toList());

        return CatResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .subCatName(subCatNames)
                .menuItems(menuItemsResponses)
                .build();
    }

    @Override
    @Transactional
    public SimpleResponse updateCat(Long catId, CatSaveRequest catSaveRequest, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        checkCatId(catId);
        checkName(catSaveRequest.name());
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF))) {
            throw new ForbiddenException("Forbidden 403");
        }
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestaurantByCatId(catId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }
        checkName(catSaveRequest.name());
        Category category = categoryRepo.getCatById(catId);
        category.setName(catSaveRequest.name());
        categoryRepo.save(category);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category with name: "+category.getName()+" successfully updated")
                .build();
    }

    @Override
    public SimpleResponse deleteCat(Long catId, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Forbidden 403");
        }
        Restaurant adminRestaurant = user.getRestaurant();
        checkCatId(catId);
        Restaurant curRestaurant = restaurantRepo.getRestaurantByCatId(catId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        Category category = categoryRepo.getCatById(catId);
        categoryRepo.delete(category);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category successfully deleted")
                .build();
    }

    @Override
    public CategoryPagination findAll(int page, int size, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        Long resId = user.getRestaurant().getId();

        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF) ||
                user.getRole().equals(Role.WAITER))) {
            throw new ForbiddenException("Forbidden 403");
        }

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Category> categories = categoryRepo.findAllCategories(resId, pageable);

        if (categories.isEmpty()) throw new NotFoundException("Categories not found");

        List<CategoriesResponse> responseList = categories.getContent().stream()
                .map(this::convertToCategory)
                .collect(Collectors.toList());

        return CategoryPagination.builder()
                .page(categories.getNumber() + 1)
                .size(categories.getTotalPages())
                .responses(responseList)
                .build();
    }

    private CategoriesResponse convertToCategory(Category category) {
        return new CategoriesResponse(
                category.getId(), category.getName()
        );
    }
}

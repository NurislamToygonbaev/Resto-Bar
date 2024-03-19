package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.*;
import restaurant.entities.*;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.CategoryRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.SubCategoryRepository;
import restaurant.services.SubCategoryService;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubCategoryServiceImpl implements SubCategoryService {
    private final SubCategoryRepository subCategoryRepo;
    private final CategoryRepository categoryRepo;
    private final CurrentUserService currentUserService;
    private final RestaurantRepository restaurantRepo;

    private void checkName(String name) {
        boolean b = subCategoryRepo.existsByName(name);
        if (b) throw new AlreadyExistsException("Category with name: " + name + " already have");
    }
    private void checkSubId(Long subId){
        subCategoryRepo.getSubCategoryId(subId);
    }

    @Override @Transactional
    public SimpleResponse saveSub(Long catId, CatSaveRequest catSaveRequest, Principal principal) {
        checkName(catSaveRequest.name());
        User user = currentUserService.adminAndChef(principal);
        categoryRepo.getCatById(catId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant restaurant = restaurantRepo.getRestaurantByCatId(catId);
        if (restaurant != null){
            currentUserService.checkForbidden(adminRestaurant, restaurant);
        }

        Category category = categoryRepo.getCatById(catId);
        SubCategory subCategory = new SubCategory();
        subCategory.setName(catSaveRequest.name());

        category.addSubCategory(subCategory);
        subCategory.setCategory(category);
        subCategoryRepo.save(subCategory);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Sub Category with name: "+subCategory.getName()+" successfully saved")
                .build();
    }

    @Override
    public SubCategoriesPagination findAllSUbCategories(Long catId, Principal principal, int page, int size) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        categoryRepo.getCatById(catId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant restaurant = restaurantRepo.getRestaurantByCatId(catId);
        currentUserService.checkForbidden(adminRestaurant, restaurant);

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SubCategory> subCategories = subCategoryRepo.findSubCategoriesById(catId, pageable);

        if (subCategories.isEmpty()) throw new NotFoundException("Sub categories not found");

        List<CategoriesResponse> collect = subCategories.getContent().stream()
                .map(this::convertToSub)
                .collect(Collectors.toList());

        Comparator<CategoriesResponse> nameComparator = Comparator
                .comparing(categoriesResponse -> categoriesResponse.name().toLowerCase());

        collect.sort(nameComparator);
        return SubCategoriesPagination.builder()
                .page(subCategories.getNumber() + 1)
                .size(subCategories.getNumberOfElements())
                .responses(collect)
                .build();
    }

    @Override
    public SubCategoriesResponse findById(Long subId, Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Restaurant adminRestaurant = user.getRestaurant();
        checkSubId(subId);
        Restaurant restaurant = restaurantRepo.getRestaurantBySubId(subId);
        currentUserService.checkForbidden(adminRestaurant, restaurant);

        SubCategory subCategory = subCategoryRepo.getSubCategoryId(subId);
        List<MenuItemsResponse> collect = subCategory.getMenuItems().stream()
                .map(this::convertToMenu)
                .collect(Collectors.toList());

        return SubCategoriesResponse.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .responses(collect)
                .build();
    }

    @Override
    public SimpleResponse update(Long subId, Principal principal, CatSaveRequest catSaveRequest) {
        User user = currentUserService.adminAndChef(principal);
        checkSubId(subId);
        checkName(catSaveRequest.name());
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant restaurant = restaurantRepo.getRestaurantBySubId(subId);
        currentUserService.checkForbidden(adminRestaurant, restaurant);

        SubCategory subCategory = subCategoryRepo.getSubCategoryId(subId);

        subCategory.setName(catSaveRequest.name());
        subCategoryRepo.save(subCategory);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Sub category successfully updated")
                .build();
    }

    @Override
    public SimpleResponse delete(Long subId, Principal principal) {
        User user = currentUserService.adminAndChef(principal);
        checkSubId(subId);
        Restaurant adminRestaurant = user.getRestaurant();
        SubCategory subCategory = subCategoryRepo.getSubCategoryId(subId);
        Restaurant restaurant = restaurantRepo.getRestaurantBySubId(subId);
        currentUserService.checkForbidden(adminRestaurant, restaurant);

        subCategoryRepo.delete(subCategory);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Sub category successfully deleted")
                .build();
    }

    @Override
    public List<SubCategoriesResponseFilter> filterWithCategory(Principal principal) {
        User user = currentUserService.adminAndChefAndWaiter(principal);
        Long resId = user.getRestaurant().getId();
        List<Category> categories = categoryRepo.findAllCategories(resId);

        Map<String, List<CategoriesResponse>> groupedResponses = new HashMap<>();

        for (Category category : categories) {
            groupedResponses.put(category.getName(), new ArrayList<>());
        }

        List<SubCategory> subCategories = subCategoryRepo.findAllSubCatById(resId);

        for (SubCategory subCategory : subCategories) {
            CategoriesResponse categoriesResponse = convertToSub(subCategory);
            String categoryName = subCategory.getCategory().getName();
            groupedResponses.get(categoryName).add(categoriesResponse);
        }

        return groupedResponses.entrySet().stream()
                .map(entry -> new SubCategoriesResponseFilter(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private MenuItemsResponse convertToMenu(MenuItem menuItem) {
        return new MenuItemsResponse(
                menuItem.getId(), menuItem.getName(), menuItem.getImage(),
                menuItem.getPrice(), menuItem.getDescription(), menuItem.isVegetarian(),
                menuItem.getQuantity()
        );
    }

    private CategoriesResponse convertToSub(SubCategory sub) {
        return new CategoriesResponse(sub.getId(), sub.getName());
    }
}

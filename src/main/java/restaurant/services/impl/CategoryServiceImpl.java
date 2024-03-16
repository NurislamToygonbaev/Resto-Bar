package restaurant.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.CatResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.Category;
import restaurant.entities.MenuItem;
import restaurant.entities.SubCategory;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.repository.CategoryRepository;
import restaurant.services.CategoryService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepo;

    private void checkName(String name){
        boolean b = categoryRepo.existsByName(name);
        if (b) throw new AlreadyExistsException("Category with name: "+name+" already have");
    }
    @Override
    public SimpleResponse saveCat(CatSaveRequest catSaveRequest) {
        checkName(catSaveRequest.name());
        Category save = categoryRepo.save(
                Category.builder()
                        .name(catSaveRequest.name())
                        .build()
        );
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category saved with name: "+save.getName())
                .build();
    }

    @Override
    public CatResponse findById(Long catId) {
        Category category = categoryRepo.getCatById(catId);

        List<String> names = new ArrayList<>();
        List<MenuItem> menuItems = new ArrayList<>();
        List<SubCategory> subCategories = category.getSubCategories();
        for (SubCategory subCategory : subCategories) {
            names.add(subCategory.getName());
            menuItems.addAll(subCategory.getMenuItems());
        }

        return CatResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .subCatName(names)
                .menuItems(menuItems)
                .build();
    }

    @Override
    public SimpleResponse updateCat(Long catId, CatSaveRequest catSaveRequest) {
        Category category = categoryRepo.getCatById(catId);
        category.setName(catSaveRequest.name());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category successfully updated")
                .build();
    }

    @Override
    public SimpleResponse deleteCat(Long catId) {
        Category category = categoryRepo.getCatById(catId);
        categoryRepo.delete(category);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Category successfully deleted")
                .build();
    }
}

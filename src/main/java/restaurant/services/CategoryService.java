package restaurant.services;

import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.CatResponse;
import restaurant.dto.response.CategoriesResponse;
import restaurant.dto.response.CategoryPagination;
import restaurant.dto.response.SimpleResponse;

import java.security.Principal;
import java.util.List;

public interface CategoryService {
    SimpleResponse saveCat(CatSaveRequest catSaveRequest, Principal principal, Long resId);

    CatResponse findById(Long catId, Principal principal);

    SimpleResponse updateCat(Long catId, CatSaveRequest catSaveRequest, Principal principal);

    SimpleResponse deleteCat(Long catId, Principal principal);

    CategoryPagination findAll(int page, int size, Principal principal);
}

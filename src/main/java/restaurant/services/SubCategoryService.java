package restaurant.services;


import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.*;

import java.security.Principal;
import java.util.List;

public interface SubCategoryService {
    SimpleResponse saveSub(Long catId, CatSaveRequest catSaveRequest, Principal principal);

    SubCategoriesPagination findAllSUbCategories(Long catId, Principal principal, int page, int size);

    SubCategoriesResponse findById(Long subId, Principal principal);

    SimpleResponse update(Long subId, Principal principal, CatSaveRequest catSaveRequest);

    SimpleResponse delete(Long subId, Principal principal);

    List<SubCategoriesResponseFilter> filterWithCategory(Principal principal);
}

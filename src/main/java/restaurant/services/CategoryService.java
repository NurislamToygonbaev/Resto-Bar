package restaurant.services;

import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.CatResponse;
import restaurant.dto.response.SimpleResponse;

public interface CategoryService {
    SimpleResponse saveCat(CatSaveRequest catSaveRequest);

    CatResponse findById(Long catId);

    SimpleResponse updateCat(Long catId, CatSaveRequest catSaveRequest);

    SimpleResponse deleteCat(Long catId);
}

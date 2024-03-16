package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.SubCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sub-cat")
public class SubCatApi {
    private final SubCategoryService subCategoryService;

//    @PostMapping("/{catId}")
//    public SimpleResponse saveSUb(@PathVariable Long catId, @RequestBody CatSaveRequest catSaveRequest){
//        return subCategoryService.saveSub(catId, catSaveRequest);
//    }
}

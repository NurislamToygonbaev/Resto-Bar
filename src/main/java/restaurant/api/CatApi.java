package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.CatSaveRequest;
import restaurant.dto.response.CatResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.CategoryService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cat")
public class CatApi {
    private final CategoryService categoryService;

    @Secured({"ADMIN", "CHEF"})
    @PostMapping
    public SimpleResponse saveCat(@RequestBody CatSaveRequest catSaveRequest){
        return categoryService.saveCat(catSaveRequest);
    }

    @GetMapping("/find/{catId}")
    public CatResponse findById(@PathVariable Long catId){
        return categoryService.findById(catId);
    }

    @Secured({"ADMIN", "CHEF"})
    @PutMapping("/{catId}")
    public SimpleResponse updateCat(@PathVariable Long catId,
                                    @RequestBody CatSaveRequest catSaveRequest,
                                    Principal principal){
        return categoryService.updateCat(catId, catSaveRequest);
    }

    @DeleteMapping("/{catId}")
    public SimpleResponse deleteCat(@PathVariable Long catId){
        return categoryService.deleteCat(catId);
    }
}

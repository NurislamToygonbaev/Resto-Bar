package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.RestPagResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rest")
public class RestaurantApi {
    private final RestaurantService restaurantService;

    @Secured("DEVELOPER")
    @GetMapping
    public RestPagResponse findAll(@RequestParam int page,
                                   @RequestParam int size){
        return restaurantService.findAll(page, size);
    }

    @Secured("DEVELOPER")
    @PostMapping
    public SimpleResponse createRestAndAdmin(@RequestBody SaveRestaurantRequest saveRestaurantRequest,
                                             Principal principal){
        return restaurantService.save(principal, saveRestaurantRequest);
    }

    @Secured("DEVELOPER")
    @GetMapping("/find-by-id/{resId}")
    public FindRestaurantResponse findById(@PathVariable Long resId, Principal principal){
        return restaurantService.findById(resId, principal);
    }

    @Secured({"DEVELOPER", "ADMIN"})
    @PutMapping("/{restId}")
    public SimpleResponse editRestaurant(@PathVariable Long restId,
                                         @RequestBody EditRestaurantRequest editRestaurantRequest,
                                         Principal principal){
        return restaurantService.editRestaurant(restId, editRestaurantRequest, principal);
    }

    @Secured({"DEVELOPER"})
    @DeleteMapping("/{resId}")
    public SimpleResponse deleteRestaurant(@PathVariable Long resId, Principal principal){
        return restaurantService.delete(resId, principal);
    }

}

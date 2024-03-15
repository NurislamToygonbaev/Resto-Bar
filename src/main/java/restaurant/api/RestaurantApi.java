package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rest")
public class RestaurantApi {
    private final RestaurantService restaurantService;

//    @Secured("DEVELOPER")
//    @GetMapping
//    public
    @Secured("DEVELOPER")
    @PostMapping
    public SimpleResponse createRestAndAdmin(@RequestBody SaveRestaurantRequest saveRestaurantRequest){
        return restaurantService.save(saveRestaurantRequest);
    }

    @Secured("DEVELOPER")
    @GetMapping("/find-by-id/{resId}")
    public FindRestaurantResponse findById(@PathVariable Long resId){
        return restaurantService.findById(resId);
    }

    @Secured({"ADMIN", "DEVELOPER"})
    @PutMapping("/{restId}")
    public SimpleResponse editRestaurant(@PathVariable Long restId,
                                         @RequestBody EditRestaurantRequest editRestaurantRequest){
        return restaurantService.editRestaurant(restId, editRestaurantRequest);
    }

    @Secured("DEVELOPER")
    @DeleteMapping("/{resId}")
    public SimpleResponse deleteRestaurant(@PathVariable Long resId){
        return restaurantService.delete(resId);
    }

}

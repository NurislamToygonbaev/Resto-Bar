package restaurant.api;

import jakarta.validation.Valid;
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
    @PostMapping
    public SimpleResponse createRestAndAdmin(@RequestBody @Valid SaveRestaurantRequest saveRestaurantRequest,
                                             Principal principal){
        return restaurantService.save(principal, saveRestaurantRequest);
    }

    @Secured({"ADMIN"})
    @PutMapping
    public SimpleResponse editRestaurant(@RequestBody @Valid EditRestaurantRequest editRestaurantRequest,
                                         Principal principal){
        return restaurantService.editRestaurant(editRestaurantRequest, principal);
    }

    @Secured({"DEVELOPER"})
    @DeleteMapping("/{resId}")
    public SimpleResponse deleteRestaurant(@PathVariable Long resId, Principal principal){
        return restaurantService.delete(resId, principal);
    }

}

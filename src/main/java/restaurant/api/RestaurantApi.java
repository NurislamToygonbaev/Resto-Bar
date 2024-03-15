package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rest")
public class RestaurantApi {
    private final RestaurantService restaurantService;

    @Secured("DEVELOPER")
    @PostMapping
    public SimpleResponse createRestAndAdmin(@RequestBody SaveRestaurantRequest saveRestaurantRequest){
        return restaurantService.save(saveRestaurantRequest);
    }
}

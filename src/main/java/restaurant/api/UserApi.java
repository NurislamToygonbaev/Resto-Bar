package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;
import restaurant.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApi {
    private final UserService userService;
    private final RestaurantService restaurantService;

//    @Secured("ADMIN")
//    @GetMapping
//    public
    @Secured("ADMIN")
    @PostMapping("/{resId}")
    public SignResponse saveUser(@RequestBody SignUpRequest signUpRequest,
                                 @PathVariable Long resId){
        return userService.saveUser(resId, signUpRequest);
    }

    @Secured("ADMIN")
    @PostMapping("/assign-user/{resId}/{userId}")
    public SimpleResponse assignUserToRes(@PathVariable Long resId,
                                          @PathVariable Long userId){
        return restaurantService.assignUserToRes(resId, userId);
    }
}

package restaurant.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.RestPagResponse;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;
import restaurant.services.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {
    private final UserService userService;
    private final RestaurantService restaurantService;

    @GetMapping
    public RestPagResponse findAll(@RequestParam int page,
                                   @RequestParam int size){
        return restaurantService.findAll(page, size);
    }

    @GetMapping("/find/{resId}")
    public FindRestaurantResponse findById(@PathVariable Long resId){
        return restaurantService.findById(resId);
    }

    @PostMapping
    public SignResponse signIn(@RequestBody @Valid SignInRequest signInRequest){
        return userService.signIn(signInRequest);
    }

    @PostMapping("/sign-up/{resId}")
    public SimpleResponse singUp(@RequestBody @Valid SignUpRequest signUpRequest,
                                 @PathVariable Long resId){
        return userService.signUp(resId, signUpRequest);
    }
}

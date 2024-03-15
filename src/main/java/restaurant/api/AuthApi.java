package restaurant.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthApi {
    private final UserService userService;
    @PostMapping("/sign-in")
    public SignResponse signIn(@RequestBody @Valid SignInRequest signInRequest){
        return userService.signIn(signInRequest);
    }

    @PostMapping("/save-user/{resId}")
    public SignResponse saveUser(@RequestBody @Valid SignUpRequest signUpRequest,
                                 @PathVariable Long resId){
        return userService.saveUser(resId, signUpRequest);
    }

    @PostMapping("/sign-up")
    public SimpleResponse singUp(@RequestBody @Valid SignUpRequest signUpRequest){
        return userService.signUp(signUpRequest);
    }
}

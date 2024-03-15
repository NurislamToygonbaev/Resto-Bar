package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.SignResponse;
import restaurant.services.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApi {
    private final UserService userService;

    @Secured("ADMIN")
    @PostMapping("/save-user/{resId}")
    public SignResponse saveUser(@RequestBody SignUpRequest signUpRequest,
                                 @PathVariable Long resId){
        return userService.saveUser(resId, signUpRequest);
    }
}

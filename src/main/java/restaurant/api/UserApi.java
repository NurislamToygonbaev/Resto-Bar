package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.PaginationUser;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.RestaurantService;
import restaurant.services.UserService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApi {
    private final UserService userService;
    private final RestaurantService restaurantService;

    @Secured("ADMIN")
    @GetMapping
    public PaginationUser findAll(@RequestParam int page,
                                  @RequestParam int size,
                                  Principal principal){
        return userService.findALlUsers(page, size, principal);
    }

    @Secured("ADMIN")
    @PostMapping("/{resId}")
    public SignResponse saveUser(@RequestBody SignUpRequest signUpRequest,
                                 @PathVariable Long resId,
                                 Principal principal){
        return userService.saveUser(resId, signUpRequest, principal);
    }

    @Secured("ADMIN")
    @GetMapping("/apps/{resId}")
    public PaginationUser findAllApps(@RequestParam int page,
                                  @RequestParam int size,
                                  Principal principal, @PathVariable Long resId){
        return userService.findALlApps(resId, page, size, principal);
    }

    @Secured("ADMIN")
    @PostMapping("/assign/{resId}/{jobId}")
    public SimpleResponse assignUserToRes(@PathVariable Long resId,
                                          @PathVariable Long jobId,
                                          Principal principal){
        return restaurantService.assignUserToRes(resId, jobId, principal);
    }

    @Secured("ADMIN")
    @PostMapping("/rejection/{resId}/{jobId}")
    public SimpleResponse rejectionApps(@PathVariable Long jobId,
                                        @PathVariable Long resId,
                                        Principal principal){
        return restaurantService.rejectionApps(resId, jobId, principal);
    }
}

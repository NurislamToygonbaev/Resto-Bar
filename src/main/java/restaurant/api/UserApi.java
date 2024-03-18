package restaurant.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.request.UpdateRequest;
import restaurant.dto.response.*;
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
    @PostMapping
    public SignResponse saveUser(@RequestBody @Valid SignUpRequest signUpRequest,
                                 Principal principal){
        return userService.saveUser(signUpRequest, principal);
    }

    @Secured("ADMIN")
    @GetMapping("/apps")
    public PaginationUser findAllApps(@RequestParam int page,
                                  @RequestParam int size,
                                  Principal principal){
        return userService.findALlApps(page, size, principal);
    }

    @Secured("ADMIN")
    @PostMapping("/assign/{jobId}")
    public SimpleResponse assignUserToRes(@PathVariable Long jobId,
                                          Principal principal){
        return restaurantService.assignUserToRes(jobId, principal);
    }

    @Secured("ADMIN")
    @PostMapping("/rejection/{jobId}")
    public SimpleResponse rejectionApps(@PathVariable Long jobId,
                                        Principal principal){
        return restaurantService.rejectionApps(jobId, principal);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @PutMapping
    public HttpResponseForUser update(Principal principal,
                                      @RequestBody @Valid UpdateRequest updateRequest){
        return userService.update(principal, updateRequest);
    }

    @Secured("ADMIN")
    @PutMapping("/update/{userId}")
    public HttpResponseForUser updateEmployees(@PathVariable Long userId,
                                               @RequestBody SignUpRequest signUpRequest,
                                               Principal principal){
        return userService.updateEmployees(userId, signUpRequest, principal);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/findEmployee/{userId}")
    public AllUsersResponse findEmployee(@PathVariable Long userId,
                                         Principal principal){
        return userService.findEmployee(userId, principal);
    }

    @Secured("ADMIN")
    @DeleteMapping("/{userId}")
    public SimpleResponse deleteEmployee(@PathVariable Long userId, Principal principal){
        return userService.deleteEmployee(userId, principal);
    }
}

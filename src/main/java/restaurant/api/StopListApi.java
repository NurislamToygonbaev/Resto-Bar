package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.response.StopListPagination;
import restaurant.dto.response.StopListResponse;
import restaurant.services.StopListService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stop-list")
public class StopListApi {
    private final StopListService stopListService;

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping
    public StopListPagination findAll(@RequestParam int page,
                                      @RequestParam int size,
                                      Principal principal){
        return stopListService.findAll(page, size, principal);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/find/{stopId}")
    public StopListResponse findById(@PathVariable Long stopId, Principal principal){
        return stopListService.findById(stopId, principal);
    }
}

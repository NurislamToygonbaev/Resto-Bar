package restaurant.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.MenuItemService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/manu")
public class MenuAPi {
    private final MenuItemService menuItemService;

    @Secured({"ADMIN", "CHEF"})
    @PostMapping("/{resId}")
    public SimpleResponse saveMenu(@PathVariable Long resId,
                                   @RequestBody SaveMenuRequest saveMenuRequest){
        return menuItemService.save(resId, saveMenuRequest);
    }
}

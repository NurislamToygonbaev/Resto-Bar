package restaurant.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.MenuItemService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuAPi {
    private final MenuItemService menuItemService;

    @Secured({"ADMIN", "CHEF"})
    @PostMapping
    public SimpleResponse saveMenu(@RequestBody @Valid SaveMenuRequest saveMenuRequest,
                                   Principal principal){
        return menuItemService.save(principal, saveMenuRequest);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/find/{menuId}")
    public MenuItemsResponse findMenuItemById(@PathVariable Long menuId, Principal principal){
        return menuItemService.findById(menuId, principal);
    }
}

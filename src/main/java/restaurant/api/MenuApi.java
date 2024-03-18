package restaurant.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import restaurant.dto.request.QuantityRequest;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.MenuPagination;
import restaurant.dto.response.SimpleResponse;
import restaurant.services.MenuItemService;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuApi {
    private final MenuItemService menuItemService;

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping
    public MenuPagination findAllMenu(Principal principal,
                                      @RequestParam int page,
                                      @RequestParam int size){
        return menuItemService.findAll(page, size, principal);
    }

    @Secured({"ADMIN", "CHEF"})
    @PostMapping("/{subId}")
    public SimpleResponse saveMenu(@RequestBody @Valid SaveMenuRequest saveMenuRequest,
                                   @PathVariable Long subId,
                                   Principal principal){
        return menuItemService.save(subId, principal, saveMenuRequest);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/find/{menuId}")
    public MenuItemsResponse findMenuItemById(@PathVariable Long menuId, Principal principal){
        return menuItemService.findById(menuId, principal);
    }

    @Secured({"ADMIN", "CHEF"})
    @PutMapping("/{menuId}")
    public SimpleResponse updateMenu(@RequestBody @Valid SaveMenuRequest saveMenuRequest,
                                     Principal principal, @PathVariable Long menuId){
        return menuItemService.updateMenu(menuId, saveMenuRequest, principal);
    }
    @Secured({"ADMIN", "CHEF"})
    @DeleteMapping("/{menuId}")
    public SimpleResponse deleteMenu(Principal principal, @PathVariable Long menuId){
        return menuItemService.deleteMenu(menuId, principal);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/search")
    public MenuPagination searchMenu(@RequestParam String keyword,
                                     Principal principal,
                                     @RequestParam int page,
                                     @RequestParam int size){
        return menuItemService.searchMenu(keyword, principal, page, size);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/filter")
    public MenuPagination filterByPrice(@RequestParam String ascOrDesc,
                                        Principal principal,
                                        @RequestParam int page,
                                        @RequestParam int size){
        return menuItemService.filterByPrice(ascOrDesc, page, size, principal);
    }

    @Secured({"ADMIN", "CHEF", "WAITER"})
    @GetMapping("/veg")
    public MenuPagination filterVegetarian(Principal principal,
                                        @RequestParam boolean trueOrFalse,
                                        @RequestParam int page,
                                        @RequestParam int size){
        return menuItemService.filterVegetarian(trueOrFalse, page, size, principal);
    }

    @Secured({"ADMIN", "CHEF"})
    @PatchMapping("/{menuId}")
    public SimpleResponse addQuantityToMenu(@PathVariable Long menuId,
                                            Principal principal,
                                            @RequestBody QuantityRequest request){
        return menuItemService.addQuantity(menuId, request, principal);
    }
}

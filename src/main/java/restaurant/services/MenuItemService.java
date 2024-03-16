package restaurant.services;

import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.SimpleResponse;

import java.security.Principal;

public interface MenuItemService {
    SimpleResponse save(Principal principal, SaveMenuRequest saveMenuRequest);

    MenuItemsResponse findById(Long menuId, Principal principal);
}

package restaurant.services;

import restaurant.dto.request.QuantityRequest;
import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.MenuItemsResponse;
import restaurant.dto.response.MenuPagination;
import restaurant.dto.response.SimpleResponse;

import java.security.Principal;

public interface MenuItemService {
    SimpleResponse save(Long subId,  Principal principal, SaveMenuRequest saveMenuRequest);

    MenuItemsResponse findById(Long menuId, Principal principal);

    MenuPagination findAll(int page, int size, Principal principal);

    SimpleResponse updateMenu(Long menuId, SaveMenuRequest saveMenuRequest, Principal principal);

    SimpleResponse deleteMenu(Long menuId, Principal principal);

    MenuPagination searchMenu(String keyword, Principal principal, int page, int size);

    MenuPagination filterByPrice(String ascOrDesc, int page, int size, Principal principal);

    MenuPagination filterVegetarian(boolean trueOrFalse, int page, int size, Principal principal);

    SimpleResponse addQuantity(Long menuId, QuantityRequest request, Principal principal);
}

package restaurant.services;

import restaurant.dto.request.SaveMenuRequest;
import restaurant.dto.response.SimpleResponse;

public interface MenuItemService {
    SimpleResponse save(Long resId, SaveMenuRequest saveMenuRequest);
}

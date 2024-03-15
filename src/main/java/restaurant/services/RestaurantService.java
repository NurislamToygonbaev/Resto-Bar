package restaurant.services;

import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.SimpleResponse;

public interface RestaurantService {
    SimpleResponse save(SaveRestaurantRequest saveRestaurantRequest);

    SimpleResponse assignUserToRes(Long resId, Long userId);

    FindRestaurantResponse findById(Long resId);

    SimpleResponse editRestaurant(Long restId, EditRestaurantRequest editRestaurantRequest);

    SimpleResponse delete(Long resId);
}

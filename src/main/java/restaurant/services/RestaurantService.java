package restaurant.services;

import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.RestPagResponse;
import restaurant.dto.response.SimpleResponse;

import java.security.Principal;

public interface RestaurantService {
    SimpleResponse save(Principal principal, SaveRestaurantRequest saveRestaurantRequest);

    SimpleResponse assignUserToRes(Long jobId, Principal principal);

    FindRestaurantResponse findById(Long resId);

    SimpleResponse editRestaurant(EditRestaurantRequest editRestaurantRequest, Principal principal);

    SimpleResponse delete(Long resId, Principal principal);

    SimpleResponse rejectionApps(Long jobId, Principal principal);

    RestPagResponse findAll(int page, int size);
}

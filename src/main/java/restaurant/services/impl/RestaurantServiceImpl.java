package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.RestaurantService;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SimpleResponse save(SaveRestaurantRequest saveRestaurantRequest) {
        boolean exists = userRepo.existsByEmail(saveRestaurantRequest.email());
        if (exists) throw new AlreadyExistsException("User with email: "+saveRestaurantRequest.email()+" already have");

        if (saveRestaurantRequest.service() < 1){
            throw new BedRequestException("It cannot be negative");
        }

        User user = new User();
        user.setFirstName(saveRestaurantRequest.firstName());
        user.setLastName(saveRestaurantRequest.lastName());
        user.setDateOfBirth(saveRestaurantRequest.dateOfBirth());
        user.setEmail(saveRestaurantRequest.email());
        user.setPassword(passwordEncoder.encode(saveRestaurantRequest.password()));
        user.setPhoneNumber(saveRestaurantRequest.phoneNumber());
        user.setExperience(saveRestaurantRequest.experience());
        user.setRole(Role.ADMIN);
        userRepo.save(user);

        Restaurant restaurant = new Restaurant();
        restaurant.addUser(user);
        restaurant.setName(saveRestaurantRequest.name());
        restaurant.setLocation(saveRestaurantRequest.location());
        restaurant.setRestType(saveRestaurantRequest.restType());
        String service = String.valueOf(saveRestaurantRequest.service());
        restaurant.setService(service+" %");
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());

        restaurantRepo.save(restaurant);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("successfully saved")
                .build();
    }

    @Override @Transactional
    public SimpleResponse assignUserToRes(Long resId, Long userId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        User user = userRepo.getUserById(userId);
        if (restaurant.getNumberOfEmployees() > 15){
            userRepo.delete(user);
            throw new BedRequestException("employees cannot be more than 15 people");
        }
        restaurant.addUser(user);
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("You have been successfully hired")
                .build();
    }

    @Override
    public FindRestaurantResponse findById(Long resId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        User user = null;
        for (User restaurantUser : restaurant.getUsers()) {
            if (restaurantUser.getRole().equals(Role.ADMIN)){
                user = restaurantUser;
            }
        }
        return FindRestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .location(restaurant.getLocation())
                .restType(restaurant.getRestType())
                .numberOfEmployees(restaurant.getNumberOfEmployees())
                .service(restaurant.getService())
                .user(user)
                .menuItems(restaurant.getMenuItems())
                .build();
    }

    @Override @Transactional
    public SimpleResponse editRestaurant(Long restId, EditRestaurantRequest request) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restId);

        restaurant.setName(request.name());
        restaurant.setLocation(request.location());
        restaurant.setRestType(request.restType());
        String service = String.valueOf(request.service());
        restaurant.setService(service+" %");
        restaurantRepo.save(restaurant);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Restaurant successfully edited")
                .build();
    }

    @Override
    public SimpleResponse delete(Long resId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        restaurantRepo.delete(restaurant);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Restaurant successfully deleted")
                .build();
    }
}

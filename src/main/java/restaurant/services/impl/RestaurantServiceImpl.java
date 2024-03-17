package restaurant.services.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import restaurant.dto.request.EditRestaurantRequest;
import restaurant.dto.request.SaveRestaurantRequest;
import restaurant.dto.response.*;
import restaurant.entities.JobApp;
import restaurant.entities.MenuItem;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.ForbiddenException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.JobAppRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.RestaurantService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JobAppRepository jobAppRepo;
    private final CurrentUserService currentUserService;

    private void checkName(String name){
        boolean exists = restaurantRepo.existsByName(name);
        if (exists) throw new AlreadyExistsException("Restaurant with name: "+name+" already have");
    }

    @Override @Transactional
    public SimpleResponse save(Principal principal, SaveRestaurantRequest saveRestaurantRequest) {
        User currentUser = currentUserService.returnCurrentUser(principal);
        checkName(saveRestaurantRequest.name());
        if (!currentUser.getRole().equals(Role.DEVELOPER)){
            throw new ForbiddenException("Forbidden 403");
        }

        boolean exists = userRepo.existsByEmail(saveRestaurantRequest.email());
        if (exists) {
            throw new AlreadyExistsException("User with email: " + saveRestaurantRequest.email() + " already have");
        }

        if (saveRestaurantRequest.service() < 1) {
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
        user.setRestaurant(restaurant);
        restaurant.setName(saveRestaurantRequest.name());
        restaurant.setLocation(saveRestaurantRequest.location());
        restaurant.setRestType(saveRestaurantRequest.restType());
        restaurant.setService(saveRestaurantRequest.service());
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());

        restaurantRepo.save(restaurant);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("successfully saved")
                .build();
    }

    @Override
    public SimpleResponse assignUserToRes(Long jobId, Principal principal) {
        User currentUser = currentUserService.returnCurrentUser(principal);
        JobApp jobApp = jobAppRepo.getJobAppById(jobId);
        if (!currentUser.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }
        Restaurant adminRestaurant = currentUser.getRestaurant();
        Restaurant curRestaurant = restaurantRepo.getRestByAppId(jobId);

        if (!curRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        Long resId = currentUser.getRestaurant().getId();
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        if (restaurant.getNumberOfEmployees() > 15) {
            throw new BedRequestException("employees cannot be more than 15 people");
        }

        User user = new User();
        user.setLastName(jobApp.getLastName());
        user.setFirstName(jobApp.getFirstName());
        user.setEmail(jobApp.getEmail());
        user.setPassword(jobApp.getPassword());
        user.setDateOfBirth(jobApp.getDateOfBirth());
        user.setPhoneNumber(jobApp.getPhoneNumber());
        user.setRole(jobApp.getRole());
        user.setExperience(jobApp.getExperience());
        userRepo.save(user);

        restaurant.addUser(user);
        user.setRestaurant(restaurant);
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());

        jobAppRepo.delete(jobApp);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("You have been successfully hired")
                .build();
    }

    @Override
    public FindRestaurantResponse findById(Long resId) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);

        User user = findAdminUser(restaurant);
        UserAminResponse response = convertAdmin(user);

        List<MenuItemsResponse> collect = restaurant.getMenuItems().stream()
                .map(this::convertMenu)
                .collect(Collectors.toList());

        return FindRestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .location(restaurant.getLocation())
                .restType(restaurant.getRestType())
                .numberOfEmployees(restaurant.getNumberOfEmployees())
                .service(String.valueOf(restaurant.getService()+" %"))
                .userAminResponse(response)
                .menuItems(collect)
                .build();
    }

    private MenuItemsResponse convertMenu(MenuItem menuItem) {
        return new MenuItemsResponse(
                menuItem.getId(), menuItem.getName(), menuItem.getImage(),
                menuItem.getPrice(), menuItem.getDescription(), menuItem.isVegetarian()
        );
    }

    private UserAminResponse convertAdmin(User user){
        return new UserAminResponse(
                user.getId(), user.getLastName(), user.getFirstName(),
                user.getDateOfBirth(), user.getEmail(), user.getPhoneNumber(),
                user.getRole(), user.getExperience()
        );
    }

    private User findAdminUser(Restaurant restaurant) {
        User user = null;
        for (User restaurantUser : restaurant.getUsers()) {
            if (restaurantUser.getRole().equals(Role.ADMIN)) {
                user = restaurantUser;
            }
        }
        return user;
    }

    @Override
    public SimpleResponse editRestaurant(EditRestaurantRequest request, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!(user.getRole().equals(Role.ADMIN))){
            throw new ForbiddenException("Forbidden 403");
        }
        checkName(request.name());

        Long restId = user.getRestaurant().getId();
        Restaurant restaurant = restaurantRepo.getRestaurantById(restId);

        restaurant.setName(request.name());
        restaurant.setLocation(request.location());
        restaurant.setRestType(request.restType());
        restaurant.setService(restaurant.getService());
        restaurantRepo.save(restaurant);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Restaurant successfully edited")
                .build();
    }

    @Override
    public SimpleResponse delete(Long resId, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!user.getRole().equals(Role.DEVELOPER)){
            throw new ForbiddenException("Forbidden 403");
        }
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        restaurantRepo.delete(restaurant);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Restaurant successfully deleted")
                .build();
    }

    @Override
    public SimpleResponse rejectionApps(Long jobId, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Forbidden 403");
        }
        JobApp jobApp = jobAppRepo.getJobAppById(jobId);
        Restaurant adminRestaurant = user.getRestaurant();
        Restaurant restaurant = restaurantRepo.getRestByAppId(jobId);

        if (!restaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }

        jobAppRepo.delete(jobApp);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Sorry, we won't be able to hire you." +
                        " when there is a vacancy, we will contact you")
                .build();
    }

    private ALlRestResponse convert(Restaurant restaurant) {
        return new ALlRestResponse(
                restaurant.getId(), restaurant.getName(), restaurant.getLocation(),
                restaurant.getRestType(),
                restaurant.getNumberOfEmployees(), restaurant.getService());
    }

    @Override
    public RestPagResponse findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Restaurant> repoAll = restaurantRepo.findAll(pageable);

        if (repoAll.isEmpty()) throw new NotFoundException("Restaurants not found");

        List<ALlRestResponse> aLlRestResponses = repoAll.getContent().stream()
                .map(this::convert)
                .collect(Collectors.toList());

        return RestPagResponse.builder()
                .page(repoAll.getNumber() + 1)
                .size(repoAll.getTotalPages())
                .responses(aLlRestResponses)
                .build();
    }
}

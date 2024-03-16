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
import restaurant.dto.response.ALlRestResponse;
import restaurant.dto.response.FindRestaurantResponse;
import restaurant.dto.response.RestPagResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.JobApp;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.ForbiddenException;
import restaurant.repository.JobAppRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.RestaurantService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
    private final RestaurantRepository restaurantRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JobAppRepository jobAppRepo;

    @Override
    public SimpleResponse save(Principal principal, SaveRestaurantRequest saveRestaurantRequest) {
        boolean exists = userRepo.existsByEmail(saveRestaurantRequest.email());
        if (exists)
            throw new AlreadyExistsException("User with email: " + saveRestaurantRequest.email() + " already have");

        if (saveRestaurantRequest.service() < 1) {
            throw new BedRequestException("It cannot be negative");
        }

        String email = principal.getName();
        User currentUser = userRepo.getByEmail(email);
        if (currentUser.getRole().equals(Role.DEVELOPER)){
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
            String service = String.valueOf(saveRestaurantRequest.service());
            restaurant.setService(service + " %");
            restaurant.setNumberOfEmployees(restaurant.getUsers().size());

            restaurantRepo.save(restaurant);

            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("successfully saved")
                    .build();
        }else throw new ForbiddenException("Forbidden 403");
    }

    @Override
    public SimpleResponse assignUserToRes(Long resId, Long jobId, Principal principal) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        JobApp jobApp = jobAppRepo.getJobAppById(jobId);

        if (restaurant.getNumberOfEmployees() > 15) {
            throw new BedRequestException("employees cannot be more than 15 people");
        }

        String email = principal.getName();
        User currentUser = userRepo.getByEmail(email);

        if (currentUser.getRole().equals(Role.ADMIN) && restaurant.getUsers().contains(currentUser)){
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
        }else throw new ForbiddenException("Forbidden 403");
    }

    @Override
    public FindRestaurantResponse findById(Long resId, Principal principal) {
        String email = principal.getName();
        User currentUser = userRepo.getByEmail(email);
        if (currentUser.getRole().equals(Role.DEVELOPER)){
            Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
            User user = null;
            for (User restaurantUser : restaurant.getUsers()) {
                if (restaurantUser.getRole().equals(Role.ADMIN)) {
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
        }else throw new ForbiddenException("Forbidden 403");
    }

    @Override
    public SimpleResponse editRestaurant(Long restId, EditRestaurantRequest request, Principal principal) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(restId);

        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user.getRole().equals(Role.ADMIN) && restaurant.getUsers().contains(user)
                || user.getRole().equals(Role.DEVELOPER)){
            restaurant.setName(request.name());
            restaurant.setLocation(request.location());
            restaurant.setRestType(request.restType());
            String service = String.valueOf(request.service());
            restaurant.setService(service + " %");
            restaurantRepo.save(restaurant);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Restaurant successfully edited")
                    .build();

        }else throw new ForbiddenException("Forbidden 403");

    }

    @Override
    public SimpleResponse delete(Long resId, Principal principal) {
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        String email = principal.getName();
        User user = userRepo.getByEmail(email);
        if (user.getRole().equals(Role.DEVELOPER)){
            restaurantRepo.delete(restaurant);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Restaurant successfully deleted")
                    .build();
        }else throw new ForbiddenException("Forbidden 403");
    }

    @Override
    public SimpleResponse rejectionApps(Long resId, Long jobId, Principal principal) {
        JobApp jobApp = jobAppRepo.getJobAppById(jobId);
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user.getRole().equals(Role.ADMIN) && restaurant.getUsers().contains(user)){
            jobAppRepo.delete(jobApp);
            return SimpleResponse.builder()
                    .httpStatus(HttpStatus.OK)
                    .message("Sorry, we won't be able to hire you." +
                            " when there is a vacancy, we will contact you")
                    .build();
        }else throw new ForbiddenException("Forbidden 403");
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
        List<ALlRestResponse> aLlRestResponses = new ArrayList<>();
        for (Restaurant restaurant : repoAll.getContent()) {
            ALlRestResponse convert = convert(restaurant);
            aLlRestResponses.add(convert);
        }
        return RestPagResponse.builder()
                .page(repoAll.getNumber() + 1)
                .size(repoAll.getTotalPages())
                .responses(aLlRestResponses)
                .build();
    }
}

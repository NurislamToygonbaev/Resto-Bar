package restaurant.services.impl;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import restaurant.config.jwt.JwtService;
import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.request.UpdateRequest;
import restaurant.dto.response.*;
import restaurant.entities.JobApp;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.RestType;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.ForbiddenException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.JobAppRepository;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.UserService;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JobAppRepository jobAppRepo;
    private final CurrentUserService currentUserService;

    @PostConstruct
    void saveDeveloper() {
        userRepo.save(
                User.builder()
                        .firstName("Muhammed")
                        .lastName("Toichubai uulu")
                        .phoneNumber("+996700700700")
                        .dateOfBirth(LocalDate.of(1900, 1, 1))
                        .experience(100)
                        .email("devops@gmail.com")
                        .password(passwordEncoder.encode("developer"))
                        .role(Role.DEVELOPER)
                        .build()
        );
    }

    private void checkEmail(String email) {
        boolean exists = userRepo.existsByEmail(email);
        if (exists) throw new AlreadyExistsException("User with email: " + email + " already have");
    }

    @Override
    public SignResponse signIn(SignInRequest signInRequest) {
        User user = userRepo.getByEmail(signInRequest.email());

        boolean matches = passwordEncoder.matches(signInRequest.password(), user.getPassword());
        if (!matches) throw new NotFoundException("Invalid password");

        return SignResponse.builder()
                .token(jwtService.createToken(user))
                .id(user.getId())
                .role(user.getRole().name())
                .httpStatus(HttpStatus.OK)
                .message("Successful login")
                .build();
    }

    private void checkAge(SignUpRequest signUpRequest) {
        if (signUpRequest.role().equals(Role.CHEF)) {
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 45 || age < 25) {
                throw new BedRequestException("The age limit should be from 25 to 45");
            }
            if (signUpRequest.experience() <= 1) {
                throw new BedRequestException("experience of at least 2 years");
            }
        } else if (signUpRequest.role().equals(Role.WAITER)) {
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 30 || age < 18) {
                throw new BedRequestException("The age limit should be from 18 to 30");
            }
        }
    }

    @Override @Transactional
    public SimpleResponse signUp(Long resId, SignUpRequest signUpRequest) {
        checkEmail(signUpRequest.email());
        checkAge(signUpRequest);
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        if (restaurant.getNumberOfEmployees() > 15){
            throw new NotFoundException("There are no vacancies");
        }
        JobApp saveApps = jobAppRepo.save(
                JobApp.builder()
                        .firstName(signUpRequest.firstName())
                        .lastName(signUpRequest.lastName())
                        .email(signUpRequest.email())
                        .password(passwordEncoder.encode(signUpRequest.password()))
                        .dateOfBirth(signUpRequest.dateOfBirth())
                        .phoneNumber(signUpRequest.phoneNumber())
                        .experience(signUpRequest.experience())
                        .role(signUpRequest.role())
                        .build()
        );
        restaurant.addJobApp(saveApps);
        saveApps.setRestaurant(restaurant);

        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("thank you for contacting us," +
                        " we will definitely consider your application")
                .build();
    }

    @Override @Transactional
    public SignResponse saveUser(SignUpRequest signUpRequest, Principal principal) {
        User currentUser = currentUserService.returnCurrentUser(principal);

        if (!currentUser.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }

        checkEmail(signUpRequest.email());
        checkAge(signUpRequest);

        Long resId = currentUser.getRestaurant().getId();
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);

        User user = new User();
        user.setFirstName(signUpRequest.firstName());
        user.setLastName(signUpRequest.lastName());
        user.setDateOfBirth(signUpRequest.dateOfBirth());
        user.setPhoneNumber(signUpRequest.phoneNumber());
        user.setEmail(signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setExperience(signUpRequest.experience());
        user.setRole(signUpRequest.role());

        restaurant.addUser(user);
        user.setRestaurant(restaurant);
        userRepo.save(user);
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());

        return SignResponse.builder()
                .token(jwtService.createToken(user))
                .email(user.getEmail())
                .id(user.getId())
                .role(user.getRole().name())
                .httpStatus(HttpStatus.OK)
                .message("Successfully saved with name: " + user.getFirstName())
                .build();
    }

    @Override @Transactional
    public PaginationUser findALlUsers(int page, int size, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        Long resId = user.getRestaurant().getId();
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Forbidden 403");
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> users = userRepo.findUserByRestaurantId(resId,pageable);
        List<AllUsersResponse> allUsersResponses = new ArrayList<>();
        for (User userFound : users.getContent()) {
            AllUsersResponse allUsersResponse = convertUser(userFound);
            allUsersResponses.add(allUsersResponse);
        }
        return PaginationUser.builder()
                .page(users.getNumber() + 1)
                .size(users.getTotalPages())
                .allUsersResponses(allUsersResponses)
                .build();
    }

    @Override @Transactional
    public PaginationUser findALlApps(int page, int size, Principal principal) {
        User user = currentUserService.returnCurrentUser(principal);
        Long restId = user.getRestaurant().getId();
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new ForbiddenException("Forbidden 403");
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<JobApp> jobApps = jobAppRepo.findAllByRestaurantId(restId,pageable);
        List<AllUsersResponse> allUsersResponses = new ArrayList<>();
        for (JobApp userFound : jobApps.getContent()) {
            AllUsersResponse allUsersResponse = convertApps(userFound);
            allUsersResponses.add(allUsersResponse);
        }
        return PaginationUser.builder()
                .page(jobApps.getNumber() + 1)
                .size(jobApps.getTotalPages())
                .allUsersResponses(allUsersResponses)
                .build();
    }

    @Override @Transactional
    public HttpResponseForUser update(Principal principal, UpdateRequest updateRequest) {
        User user = currentUserService.returnCurrentUser(principal);
        user.setLastName(updateRequest.lastName());
        user.setFirstName(updateRequest.firstName());
        user.setEmail(updateRequest.email());
        user.setPassword(passwordEncoder.encode(updateRequest.password()));
        user.setPhoneNumber(updateRequest.phoneNumber());
        user.setExperience(updateRequest.experience());
        user.setDateOfBirth(updateRequest.dateOfBirth());
        userRepo.save(user);
        return HttpResponseForUser.builder()
                .token(jwtService.createToken(user))
                .email(user.getEmail())
                .role(user.getRole())
                .simpleResponse(SimpleResponse.builder()
                        .httpStatus(HttpStatus.OK)
                        .message("successFully updated your profile")
                        .build())
                .build();
    }

    @Override @Transactional
    public HttpResponseForUser updateEmployees(Long userId, SignUpRequest request, Principal principal) {
        User admin = currentUserService.returnCurrentUser(principal);
        if (!admin.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }
        User user = userRepo.getUserById(userId);

        user.setFirstName(request.firstName());
        user.setEmail(request.email());
        user.setLastName(request.lastName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhoneNumber(request.phoneNumber());
        user.setExperience(request.experience());
        user.setDateOfBirth(request.dateOfBirth());
        user.setRole(request.role());
        userRepo.save(user);

        return HttpResponseForUser.builder()
                .token(jwtService.createToken(user))
                .role(user.getRole())
                .email(user.getEmail())
                .simpleResponse(SimpleResponse.builder()
                        .httpStatus(HttpStatus.OK)
                        .message("Employee with name: "+user.getFirstName()+" Successfully updated")
                        .build())
                .build();
    }

    @Override
    public AllUsersResponse findEmployee(Long userId, Principal principal) {
        User currentUser = currentUserService.returnCurrentUser(principal);
        if (!currentUser.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }

        User user = userRepo.getUserById(userId);

        return AllUsersResponse.builder()
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .experience(user.getExperience())
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();
    }

    @Override
    public SimpleResponse deleteEmployee(Long userId, Principal principal) {
        User currentUser = currentUserService.returnCurrentUser(principal);
        if (!currentUser.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }
        User user = userRepo.getUserById(userId);

        userRepo.delete(user);
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("Employee with name: "+user.getFirstName()+" Successfully deleted")
                .build();
    }

    private AllUsersResponse convertUser(User user) {
        return new AllUsersResponse(
                user.getId(), user.getLastName(), user.getFirstName(), user.getDateOfBirth(),
                user.getEmail(), user.getPhoneNumber(), user.getRole(),
                user.getExperience()
        );
    }
    private AllUsersResponse convertApps(JobApp user) {
        return new AllUsersResponse(
                user.getId(), user.getLastName(), user.getFirstName(), user.getDateOfBirth(),
                user.getEmail(), user.getPhoneNumber(), user.getRole(),
                user.getExperience()
        );
    }
}

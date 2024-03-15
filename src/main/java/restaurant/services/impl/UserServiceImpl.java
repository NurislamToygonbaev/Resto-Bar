package restaurant.services.impl;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;
import restaurant.config.jwt.JwtService;
import restaurant.dto.request.SignInRequest;
import restaurant.dto.request.SignUpRequest;
import restaurant.dto.response.SignResponse;
import restaurant.dto.response.SimpleResponse;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.RestType;
import restaurant.entities.enums.Role;
import restaurant.exceptions.AlreadyExistsException;
import restaurant.exceptions.BedRequestException;
import restaurant.exceptions.NotFoundException;
import restaurant.repository.RestaurantRepository;
import restaurant.repository.UserRepository;
import restaurant.services.UserService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepo;
    private final RestaurantRepository restaurantRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostConstruct @Transactional
    void saveAdmin() {
        User admin = new User("Muhammed", "Toichubai uulu",
                LocalDate.of(1997, 1, 1), "muhammed@gmail.com",
                passwordEncoder.encode("admin123"), "+99675555352",
                Role.ADMIN, 6);
        userRepo.save(admin);

        Restaurant restaurant = new Restaurant();
        restaurant.addUser(admin);

        restaurant.setName("TALISMAN");
        restaurant.setLocation("Bishkek");
        restaurant.setRestType(RestType.EUROPEAN);
        restaurant.setService(25);
        restaurant.setNumberOfEmployees(restaurant.getUsers().size());
        restaurantRepo.save(restaurant);
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

    @Override
    public SimpleResponse signUp(SignUpRequest signUpRequest) {
        checkEmail(signUpRequest.email());
        if (signUpRequest.role().equals(Role.CHEF)){
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 45 || age <= 25){
                throw new BedRequestException("The age limit should be from 25 to 45");
            }
        }
        if (signUpRequest.role().equals(Role.WAITER)){
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 30 || age <= 18){
                throw new BedRequestException("The age limit should be from 18 to 30");
            }
        }
        userRepo.save(
                User.builder()
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
        return SimpleResponse.builder()
                .httpStatus(HttpStatus.OK)
                .message("thank you for contacting us," +
                        " we will definitely consider your application")
                .build();
    }

    @Override
    @Transactional
    public SignResponse saveUser(Long resId, SignUpRequest signUpRequest) {
        checkEmail(signUpRequest.email());
        if (signUpRequest.role().equals(Role.CHEF)){
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 45 || age <= 25){
                throw new BedRequestException("The age limit should be from 25 to 45");
            }
        }
        if (signUpRequest.role().equals(Role.WAITER)){
            int year = signUpRequest.dateOfBirth().getYear();
            int currentYear = LocalDate.now().getYear();
            int age = currentYear - year;
            if (age >= 30 || age <= 18){
                throw new BedRequestException("The age limit should be from 18 to 30");
            }
        }
        Restaurant restaurant = restaurantRepo.getRestaurantById(resId);
        User user = new User();
        user.setFirstName(signUpRequest.firstName());
        user.setLastName(signUpRequest.lastName());
        user.setDateOfBirth(signUpRequest.dateOfBirth());
        user.setEmail(signUpRequest.email());
        user.setPassword(passwordEncoder.encode(signUpRequest.password()));
        user.setExperience(signUpRequest.experience());
        user.setRole(signUpRequest.role());

        restaurant.addUser(user);
        userRepo.save(user);
        return SignResponse.builder()
                .token(jwtService.createToken(user))
                .email(user.getEmail())
                .id(user.getId())
                .role(user.getRole().name())
                .httpStatus(HttpStatus.OK)
                .message("Successfully saved with name: " + user.getFirstName())
                .build();
    }
}

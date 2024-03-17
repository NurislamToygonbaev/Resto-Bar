package restaurant.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.entities.Restaurant;
import restaurant.entities.User;
import restaurant.entities.enums.Role;
import restaurant.exceptions.ForbiddenException;
import restaurant.repository.UserRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepo;

    public void devops(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }
        if (!user.getRole().equals(Role.DEVELOPER)){
            throw new ForbiddenException("Forbidden 403");
        }
    }

    public User adminUser(Principal principal){
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }
        if (!user.getRole().equals(Role.ADMIN)){
            throw new ForbiddenException("Forbidden 403");
        }
        return user;
    }
    public User adminAndChef(Principal principal){
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF))){
            throw new ForbiddenException("Forbidden 403");
        }
        return user;
    }
    public User adminAndChefAndWaiter(Principal principal){
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }
        if (!(user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.CHEF) ||
                user.getRole().equals(Role.WAITER))){
            throw new ForbiddenException("Forbidden 403");
        }
        return user;
    }
    public void checkForbidden(Restaurant adminRestaurant, Restaurant userRestaurant){
        if (!userRestaurant.equals(adminRestaurant)) {
            throw new ForbiddenException("Forbidden 403 - You are not allowed to delete employees from other restaurants");
        }
    }
}

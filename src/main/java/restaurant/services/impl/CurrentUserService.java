package restaurant.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import restaurant.entities.User;
import restaurant.repository.UserRepository;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class CurrentUserService {
    private final UserRepository userRepo;

    public User returnCurrentUser(Principal principal) {
        if (principal == null) {
            throw new IllegalArgumentException("Principal cannot be null");
        }
        String email = principal.getName();
        User user = userRepo.getByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }

        return user;
    }
}

package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.User;
import restaurant.exceptions.NotFoundException;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email =:email")
    Optional<User> findByEmail(String email);

    @Query("select u from User u where u.id =:userId")
    Optional<User> findUserById(Long userId);

    default User getByEmail(String email){
        return findByEmail(email).orElseThrow(() ->
                new NotFoundException("User with email: "+email+" not found"));
    }

    default User getUserById(Long userId){
        return findUserById(userId).orElseThrow(() ->
                new NotFoundException("User with id: "+userId+" not found"));
    }

    boolean existsByEmail(String email);
}
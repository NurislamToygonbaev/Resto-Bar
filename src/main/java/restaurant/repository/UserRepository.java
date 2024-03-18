package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.User;
import restaurant.exceptions.NotFoundException;

import java.util.List;
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

    @Query("select u from User u where u.restaurant.id =:resId")
    List<User> findAllById(Long resId);
    default Page<User> findUserByRestaurantId(Long resId, Pageable pageable){
        List<User> users = findAllById(resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), users.size());
        return new PageImpl<>(users.subList(start, end), pageable, users.size());
    }

}
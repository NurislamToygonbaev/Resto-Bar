package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.MenuItem;
import restaurant.exceptions.NotFoundException;

import java.util.Optional;

public interface
MenuItemRepository extends JpaRepository<MenuItem, Long> {
    boolean existsByName(String name);

    @Query("select m from MenuItem m where m.id =:menuId")
    Optional<MenuItem> findMenuItem(Long menuId);
    default MenuItem getMenuById(Long menuId){
        return findMenuItem(menuId).orElseThrow(() ->
                new NotFoundException("Menu with id: "+menuId+" not found"));
    }
}
package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entities.MenuItem;

public interface
MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
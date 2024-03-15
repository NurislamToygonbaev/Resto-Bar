package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entities.StopList;

public interface StopListRepository extends JpaRepository<StopList, Long> {
}
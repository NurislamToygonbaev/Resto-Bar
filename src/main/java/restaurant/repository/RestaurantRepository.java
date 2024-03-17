package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.Restaurant;
import restaurant.exceptions.NotFoundException;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query("select r from Restaurant r where r.id =:resId")
    Optional<Restaurant> findRestaurantByById(Long resId);
    default Restaurant getRestaurantById(Long resId){
        return findRestaurantByById(resId).orElseThrow(() ->
                new NotFoundException("Restaurant with id: "+resId+" not found"));
    }

    @Query("select r from Restaurant r join r.menuItems m where m.subCategory.id =:subId")
    Restaurant getRestaurantBySubId(Long subId);

    @Query("select r from Restaurant r join r.menuItems m where m.subCategory.category.id = :catId")
    Restaurant getRestaurantByCatId(Long catId);

    @Query("select r from Restaurant r join r.jobApps j where j.id =:jobId")
    Restaurant getRestByAppId(Long jobId);

    @Query("select r from Restaurant r join r.menuItems m where m.id =:menuId")
    Restaurant getRestByMenuId(Long menuId);

    boolean existsByName(String name);
}
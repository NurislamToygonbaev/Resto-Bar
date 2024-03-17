package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.StopList;
import restaurant.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface StopListRepository extends JpaRepository<StopList, Long> {

    @Query("select s from StopList s where s.menuItem.restaurant.id =:resId")
    List<StopList> getStopListsByRestaurant(Long resId);

    default Page<StopList> findStopListsByResId(Long resId, Pageable pageable){
        List<StopList> lists = getStopListsByRestaurant(resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), lists.size());
        return new PageImpl<>(lists.subList(start, end), pageable, lists.size());
    }

    @Query("select s from StopList s where s.id =:stopId")
    Optional<StopList> findStopListById(Long stopId);

    default StopList getStopListById(Long stopId){
        return findStopListById(stopId).orElseThrow(() ->
                new NotFoundException("Stop list with id: "+stopId+" not found"));
    }
}
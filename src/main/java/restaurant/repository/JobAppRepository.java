package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.JobApp;
import restaurant.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface JobAppRepository extends JpaRepository<JobApp, Long> {

    @Query("select j from JobApp j where j.id =:jobId")
    Optional<JobApp> findJobAppById(Long jobId);

    default JobApp getJobAppById(Long jobId){
        return findJobAppById(jobId).orElseThrow(() ->
                new NotFoundException("Not found"));
    }
    @Query("select j from JobApp j where j.restaurant.id =:restId")
    List<JobApp> findAll(Long restId);

    default Page<JobApp> findAllByRestaurantId(Long restId, Pageable pageable){
        List<JobApp> jobApps = findAll(restId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), jobApps.size());
        return new PageImpl<>(jobApps.subList(start, end), pageable, jobApps.size());
    }
}
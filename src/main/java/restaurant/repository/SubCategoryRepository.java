package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.SubCategory;
import restaurant.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
    boolean existsByName(String name);

    @Query("select s from SubCategory s where s.category.id =:catId")
    List<SubCategory> findAllSubCategories(Long catId);
    default Page<SubCategory> findSubCategoriesById(Long catId, Pageable pageable){
        List<SubCategory> list = findAllSubCategories(catId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        return new PageImpl<>(list.subList(start, end), pageable, list.size());
    }

    @Query("select s from SubCategory s where s.id =:subId")
    Optional<SubCategory> findSubCategoryById(Long subId);
    
    default SubCategory getSubCategoryId(Long subId){
        return findSubCategoryById(subId).orElseThrow(() ->
                new NotFoundException("Sub category with id: "+subId+" not found"));
    }

    @Query("select s from SubCategory s join s.menuItems m where m.restaurant.id =:resId")
    List<SubCategory> findAllSubCatById(Long resId);
}
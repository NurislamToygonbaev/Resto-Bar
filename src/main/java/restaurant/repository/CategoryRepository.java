package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.entities.Category;
import restaurant.exceptions.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    @Query("select c from Category c where c.id =:catId")
    Optional<Category> findCatById(Long catId);

    default Category getCatById(Long catId){
        return findCatById(catId).orElseThrow(() ->
                new NotFoundException("Category with id: "+catId+" not found"));
    }

    @Query("select c from Category c join c.subCategories s join s.menuItems m " +
            " where m.restaurant.id =:resId")
    List<Category> findAllCategories(Long resId);

    default Page<Category> findAllCategories(Long resId, Pageable pageable){
        List<Category> categories = findAllCategories(resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categories.size());
        return new PageImpl<>(categories.subList(start, end), pageable, categories.size());
    }
}
package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entities.Category;
import restaurant.exceptions.NotFoundException;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);

    Optional<Category> findCatById(Long catId);

    default Category getCatById(Long catId){
        return findCatById(catId).orElseThrow(() ->
                new NotFoundException("Category with id: "+catId+" not found"));
    }
}
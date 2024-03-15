package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entities.SubCategory;

public interface SubCategoryRepository extends JpaRepository<SubCategory, Long> {
}
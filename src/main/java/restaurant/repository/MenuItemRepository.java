package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurant.dto.response.MenuItemsResponseForCheque;
import restaurant.entities.MenuItem;
import restaurant.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
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

    @Query("select m from MenuItem m where m.restaurant.id =:resId")
    List<MenuItem> findAllMenu(Long resId);

    default Page<MenuItem> findMenuByRestaurantId(Long resId, Pageable pageable){
        List<MenuItem> menuItems = findAllMenu(resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), menuItems.size());
        return new PageImpl<>(menuItems.subList(start, end), pageable, menuItems.size());
    }

    @Query("""
            select m from MenuItem m
                where (m.name ilike (:keyword)
                or m.description ilike (:keyword)
                or m.image ilike (:keyword)
                or m.subCategory.name ilike (:keyword)
                or m.subCategory.category.name ilike (:keyword))
                and m.restaurant.id = :resId
            """)
    List<MenuItem> searchMenuItemBy(Long resId, String keyword);

    default Page<MenuItem> findMenuById(String keyword, Long resId, Pageable pageable){
        List<MenuItem> menuItems = searchMenuItemBy(resId, keyword);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), menuItems.size());
        return new PageImpl<>(menuItems.subList(start, end), pageable, menuItems.size());
    }

    @Query("""
            select m from MenuItem m where m.restaurant.id = :resId order by
            case when :word = 'asc' then m.price end asc,
            case when :word = 'desc' then m.price end desc
            """)
    List<MenuItem> filter(String word, Long resId);

    default Page<MenuItem> filterByPrice(String keyword, Long resId, Pageable pageable){
        List<MenuItem> filter = filter(keyword, resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filter.size());
        return new PageImpl<>(filter.subList(start, end), pageable, filter.size());
    }

    @Query("""
            select m from MenuItem m where m.restaurant.id = :resId and m.isVegetarian = :vegetarian
            """)
    List<MenuItem> vegetarian(boolean trueOrFalse, Long resId);

    default Page<MenuItem> filterByVegetarian(boolean trueOrFalse, Long resId, Pageable pageable){
        List<MenuItem> vegetarian = vegetarian(trueOrFalse, resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), vegetarian.size());
        return new PageImpl<>(vegetarian.subList(start, end), pageable, vegetarian.size());
    }

    @Query("select m from MenuItem m where m.id in :menuIds")
    List<MenuItem> getMenuItemsByIds(List<Long> menuIds);

    @Query("""
            select new restaurant.dto.response.MenuItemsResponseForCheque(
            m.name, m.image, m.price, m.description, m.isVegetarian)
            from MenuItem m where m.id in :menuIds
            """)
    List<MenuItemsResponseForCheque> getConvertToItems(List<Long> menuIds);
}
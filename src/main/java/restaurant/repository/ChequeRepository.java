package restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import restaurant.dto.response.MenuItemsResponseForCheque;
import restaurant.entities.Cheque;
import restaurant.exceptions.NotFoundException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {

    @Query("select c from Cheque c where c.id =:chequeId")
    Optional<Cheque> findChequeById(Long chequeId);

    default Cheque getChequeById(Long chequeId){
        return findChequeById(chequeId).orElseThrow(() ->
                new NotFoundException("Cheque with id: "+chequeId+" not found"));
    }

    @Query("select c from Cheque c where c.user.restaurant.id =:resId")
    List<Cheque> findAllChequesByResId(Long resId);

    default Page<Cheque> getAllChequesByResId(Long resId, Pageable pageable){
        List<Cheque> cheques = findAllChequesByResId(resId);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), cheques.size());
        return new PageImpl<>(cheques.subList(start, end), pageable, cheques.size());
    }

    @Query("""
            select new restaurant.dto.response.MenuItemsResponseForCheque(
            m.name, m.image, m.price, m.description, m.isVegetarian)
            from Cheque c join c.menuItems m where c.id =:chequeId
            """)
    List<MenuItemsResponseForCheque> convertToMenu(Long chequeId);
}
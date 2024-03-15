package restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import restaurant.entities.Cheque;

public interface ChequeRepository extends JpaRepository<Cheque, Long> {
}
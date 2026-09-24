package desphub.pds.backend.repositories;

import desphub.pds.backend.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IExpenseRepository extends JpaRepository<Expense, Long> {

    Optional<Expense> findByIdAndOfficeId(Long id, Long officeId);

    List<Expense> findByDateBetweenOrderByDateDesc(LocalDate from, LocalDate to);

    List<Expense> findAllByOrderByDateDesc();
}

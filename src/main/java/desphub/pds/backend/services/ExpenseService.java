package desphub.pds.backend.services;

import desphub.pds.backend.dtos.expenses.CreateExpenseDTO;
import desphub.pds.backend.dtos.expenses.ExpenseResponseDTO;
import desphub.pds.backend.dtos.expenses.UpdateExpenseDTO;
import desphub.pds.backend.mappers.ExpenseMapper;
import desphub.pds.backend.models.Expense;
import desphub.pds.backend.repositories.IExpenseRepository;
import desphub.pds.backend.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ExpenseService {

    private final IExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final CurrentUser currentUser;

    public ExpenseService(IExpenseRepository expenseRepository,
                          ExpenseMapper expenseMapper,
                          CurrentUser currentUser) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.currentUser = currentUser;
    }

    @Transactional
    public ExpenseResponseDTO create(CreateExpenseDTO dto) {
        Expense expense = expenseMapper.toEntity(dto);
        expense.setOfficeId(currentUser.requireOfficeId());
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponseDTO> list(LocalDate from, LocalDate to) {
        List<Expense> expenses = (from != null && to != null)
                ? expenseRepository.findByDateBetweenOrderByDateDesc(from, to)
                : expenseRepository.findAllByOrderByDateDesc();
        return expenses.stream().map(expenseMapper::toResponse).toList();
    }

    @Transactional
    public ExpenseResponseDTO update(Long id, UpdateExpenseDTO dto) {
        Expense expense = findEntityOr404(id);
        expenseMapper.updateEntity(dto, expense);
        return expenseMapper.toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public void delete(Long id) {
        expenseRepository.delete(findEntityOr404(id));
    }

    private Expense findEntityOr404(Long id) {
        if (currentUser.isAdmin()) {
            return expenseRepository.findById(id).orElseThrow(() -> notFound(id));
        }
        return expenseRepository.findByIdAndOfficeId(id, currentUser.officeId()).orElseThrow(() -> notFound(id));
    }

    private ResponseStatusException notFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Despesa não encontrada: " + id);
    }
}

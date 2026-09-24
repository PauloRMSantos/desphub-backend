package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.expenses.CreateExpenseDTO;
import desphub.pds.backend.dtos.expenses.ExpenseResponseDTO;
import desphub.pds.backend.dtos.expenses.UpdateExpenseDTO;
import desphub.pds.backend.services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('FINANCIAL_READ')")
    public ResponseEntity<List<ExpenseResponseDTO>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(expenseService.list(from, to));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('FINANCIAL_WRITE')")
    public ResponseEntity<ExpenseResponseDTO> create(@Valid @RequestBody CreateExpenseDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCIAL_WRITE')")
    public ResponseEntity<ExpenseResponseDTO> update(@PathVariable Long id,
                                                     @Valid @RequestBody UpdateExpenseDTO dto) {
        return ResponseEntity.ok(expenseService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FINANCIAL_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

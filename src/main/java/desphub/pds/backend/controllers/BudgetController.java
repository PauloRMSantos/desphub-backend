package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.budgets.BudgetGetResponseDTO;
import desphub.pds.backend.dtos.budgets.BudgetResponseDTO;
import desphub.pds.backend.dtos.budgets.CreateBudgetDTO;
import desphub.pds.backend.dtos.budgets.UpdateBudgetDTO;
import desphub.pds.backend.services.BudgetService;
import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('BUDGETS_WRITE')")
    public ResponseEntity<BudgetResponseDTO> create(@Valid @RequestBody CreateBudgetDTO dto) {
        BudgetResponseDTO created = budgetService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('BUDGETS_READ')")
    public ResponseEntity<List<BudgetGetResponseDTO>> findAll() {
        return ResponseEntity.ok(budgetService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGETS_READ')")
    public ResponseEntity<BudgetGetResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(budgetService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGETS_WRITE')")
    public ResponseEntity<BudgetResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody UpdateBudgetDTO dto) {
        return ResponseEntity.ok(budgetService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BUDGETS_WRITE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

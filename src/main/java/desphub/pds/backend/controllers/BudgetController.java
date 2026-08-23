package desphub.pds.backend.controllers;

import desphub.pds.backend.dtos.BudgetResponseDTO;
import desphub.pds.backend.dtos.CreateBudgetDTO;
import desphub.pds.backend.services.BudgetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public ResponseEntity<BudgetResponseDTO> create(@Valid @RequestBody CreateBudgetDTO dto) {
        BudgetResponseDTO created = budgetService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

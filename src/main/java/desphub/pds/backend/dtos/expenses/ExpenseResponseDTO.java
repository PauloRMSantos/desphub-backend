package desphub.pds.backend.dtos.expenses;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseResponseDTO {

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String category;
}

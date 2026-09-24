package desphub.pds.backend.dtos.expenses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CreateExpenseDTO {

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    private String category;
}

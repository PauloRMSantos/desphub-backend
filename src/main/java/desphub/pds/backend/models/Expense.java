package desphub.pds.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expense")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @NotBlank
    private String description;

    @NotNull
    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(name = "expense_date")
    private LocalDate date;

    private String category;
}

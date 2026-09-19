package desphub.pds.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;

@Entity
@Table(name = "service")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @NotBlank
    @Column(name = "name")
    private String serviceName;

    @NotNull
    @Column(name = "default_price", precision = 12, scale = 2)
    private BigDecimal price;
}

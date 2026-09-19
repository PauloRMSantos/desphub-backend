package desphub.pds.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;

@Entity
@Table(name = "client")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @NotBlank
    private String name;

    @NotBlank
    private String telephone;

    private String cpfCnpj;

    private String address;
}

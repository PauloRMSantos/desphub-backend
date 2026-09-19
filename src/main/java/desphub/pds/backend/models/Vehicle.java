package desphub.pds.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;


@Entity
@Table(name = "vehicle")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    private String plate;

    @NotBlank
    private String brand;

    @NotBlank
    private String model;

    @NotBlank
    private String fabricationAndModel;

    @NotBlank
    private String color;

    private String renavam;

    @NotBlank
    private String chassis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = true)
    private Client client;
}

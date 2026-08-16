package Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "vehicle")
@Getter
@Setter
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

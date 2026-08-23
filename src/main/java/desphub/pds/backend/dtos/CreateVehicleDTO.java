package desphub.pds.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVehicleDTO {

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

    private Long clientId;
}

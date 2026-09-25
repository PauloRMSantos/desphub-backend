package desphub.pds.backend.dtos.vehicles;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVehicleDTO {

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

    private String chassis;

    private Long clientId;

    // Chassi é obrigatório apenas quando o veículo não tem placa (ex.: 0 km).
    @AssertTrue(message = "Informe o chassi quando o veículo não tiver placa")
    public boolean isChassisPresentWhenNoPlate() {
        boolean hasPlate = plate != null && !plate.isBlank();
        boolean hasChassis = chassis != null && !chassis.isBlank();
        return hasPlate || hasChassis;
    }
}

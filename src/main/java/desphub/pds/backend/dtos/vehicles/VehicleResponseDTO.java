package desphub.pds.backend.dtos.vehicles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleResponseDTO {

    private Long id;
    private String plate;
    private String brand;
    private String model;
    private String fabricationAndModel;
    private String color;
    private String renavam;
    private String chassis;
    private Long clientId;
}

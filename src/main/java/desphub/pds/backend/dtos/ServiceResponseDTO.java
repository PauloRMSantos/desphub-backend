package desphub.pds.backend.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServiceResponseDTO {

    private Long id;
    private String serviceName;
    private BigDecimal price;
}

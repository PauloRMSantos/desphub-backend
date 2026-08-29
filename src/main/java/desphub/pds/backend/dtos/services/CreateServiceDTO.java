package desphub.pds.backend.dtos.services;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateServiceDTO {

    @NotBlank
    private String serviceName;

    @NotNull
    private BigDecimal price;
}

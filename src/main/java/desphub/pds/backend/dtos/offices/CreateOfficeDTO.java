package desphub.pds.backend.dtos.offices;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOfficeDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String cpfCnpj;
}

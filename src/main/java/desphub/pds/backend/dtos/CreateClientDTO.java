package desphub.pds.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateClientDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String telephone;

    private String cpfCnpj;

    private String address;
}

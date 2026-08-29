package desphub.pds.backend.dtos.clients;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateClientDTO {

    @NotBlank
    private String name;

    @NotBlank
    private String telephone;

    private String cpfCnpj;

    private String address;
}

package desphub.pds.backend.dtos.clients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponseDTO {

    private Long id;
    private String name;
    private String telephone;
    private String cpfCnpj;
    private String address;
}

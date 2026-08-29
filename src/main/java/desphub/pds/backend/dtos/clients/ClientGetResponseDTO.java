package desphub.pds.backend.dtos.clients;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientGetResponseDTO {

    private Long id;
    private String name;
    private String telephone;
    private String cpfCnpj;
    private String address;
}

package desphub.pds.backend.dtos.offices;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class OfficeResponseDTO {

    private Long id;
    private String name;
    private String cpfCnpj;
    private boolean active;
    private Instant createdAt;
}

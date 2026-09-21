package desphub.pds.backend.models;

import desphub.pds.backend.security.EncryptedStringConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "govbr_session")
@Getter
@Setter
public class GovbrSession {

    @Id
    @Column(name = "office_id")
    private Long officeId;

    @Column(nullable = false)
    private boolean connected;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "bearer", columnDefinition = "text")
    @Convert(converter = EncryptedStringConverter.class)
    private String bearer;

    @Column(name = "user_id")
    @Convert(converter = EncryptedStringConverter.class)
    private String userId;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}

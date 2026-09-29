package desphub.pds.backend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;

import java.time.Instant;

@Entity
@Table(name = "generated_document")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class GeneratedDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @Column(name = "template_id")
    private Long templateId;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "client_name")
    private String clientName;

    @Column(name = "vehicle_id")
    private Long vehicleId;

    @Column(name = "resolved_content", columnDefinition = "text", nullable = false)
    private String resolvedContent;

    @Column(name = "selected_block_ids")
    private String selectedBlockIds;

    @Column(name = "filled_values", columnDefinition = "text")
    private String filledValues;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}

package desphub.pds.backend.models;

import desphub.pds.backend.enums.TemplateCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "document_template")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class DocumentTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @NotBlank
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TemplateCategory category;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClauseBlock> blocks = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateVariable> variables = new ArrayList<>();

    public void addBlock(ClauseBlock block) {
        block.setTemplate(this);
        blocks.add(block);
    }

    public void addVariable(TemplateVariable variable) {
        variable.setTemplate(this);
        variables.add(variable);
    }
}

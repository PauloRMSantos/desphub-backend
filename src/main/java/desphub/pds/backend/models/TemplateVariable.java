package desphub.pds.backend.models;

import desphub.pds.backend.enums.VariableSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "template_variable")
@Getter
@Setter
public class TemplateVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id")
    private DocumentTemplate template;

    @Column(name = "var_key", nullable = false)
    private String varKey;

    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VariableSource source;

    @Column(name = "source_field")
    private String sourceField;

    @Column(nullable = false)
    private boolean required;
}

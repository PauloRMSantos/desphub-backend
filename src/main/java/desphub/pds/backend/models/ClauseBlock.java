package desphub.pds.backend.models;

import desphub.pds.backend.enums.SelectionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clause_block")
@Getter
@Setter
public class ClauseBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "template_id")
    private DocumentTemplate template;

    private String label;

    @Column(columnDefinition = "text", nullable = false)
    private String body;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    @Column(name = "default_selected", nullable = false)
    private boolean defaultSelected;

    @Column(name = "group_key")
    private String groupKey;

    @Column(name = "group_label")
    private String groupLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "selection_type")
    private SelectionType selectionType;

    @Column(name = "group_required", nullable = false)
    private boolean groupRequired;

    @Column(name = "group_sort_order", nullable = false)
    private int groupSortOrder;
}

package desphub.pds.backend.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import desphub.pds.backend.enums.OrderStatusEnum;
import org.hibernate.annotations.Filter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_order")
@Getter
@Setter
@Filter(name = "officeFilter", condition = "office_id = :officeId")
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "office_id", nullable = false)
    private Long officeId;

    @NotBlank
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatusEnum orderStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_budget_id")
    private Budget originBudget;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceOrderItem> items = new ArrayList<>();

    @Column(name = "services_total", precision = 12, scale = 2)
    private BigDecimal servicesTotal;

    @Column(name = "fees_total", precision = 12, scale = 2)
    private BigDecimal feesTotal;

    @Column(name = "total", precision = 12, scale = 2)
    private BigDecimal total;

    public void addItem(ServiceOrderItem item) {
        items.add(item);
        item.setOrder(this);
    }
}

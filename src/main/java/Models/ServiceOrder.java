package Models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import Enum.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "service_order")
@Getter
@Setter
public class ServiceOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String code;

    @Enumerated(EnumType.STRING)
    private OrderStatusEnum orderStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)    // nullable: OS pode nascer do zero
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

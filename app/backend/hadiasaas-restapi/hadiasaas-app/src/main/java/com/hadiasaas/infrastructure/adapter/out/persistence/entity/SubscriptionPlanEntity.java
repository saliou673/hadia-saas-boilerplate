package com.hadiasaas.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mapping the {@code subscription_plan} table (features stored in {@code subscription_plan_feature}).
 */
@Entity
@Table(name = "subscription_plan")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class SubscriptionPlanEntity extends AuditableEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "monthly_price", precision = 19, scale = 4)
    private BigDecimal monthlyPrice;

    @Column(name = "yearly_price", precision = 19, scale = 4)
    private BigDecimal yearlyPrice;

    @Column(name = "lifetime_price", precision = 19, scale = 4)
    private BigDecimal lifetimePrice;

    /**
     * Price for the custom billing cycle; {@code null} if no custom cycle is offered.
     */
    @Column(name = "price", precision = 19, scale = 4)
    private BigDecimal price;

    /**
     * Duration in days for the custom billing cycle; {@code null} if no custom cycle is offered.
     */
    @Column(name = "duration_days")
    private Integer durationDays;

    @Column(name = "currency_code", nullable = false, length = 10)
    private String currencyCode;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "subscription_plan_feature",
            joinColumns = @JoinColumn(name = "plan_id")
    )
    @OrderColumn(name = "position")
    @Column(name = "feature", nullable = false)
    private List<String> features = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubscriptionPlanEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

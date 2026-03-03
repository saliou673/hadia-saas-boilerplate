package com.maitrisetcf.infrastructure.adapter.out.persistence.entity;

import com.maitrisetcf.domain.enumerations.SubscriptionPlanType;
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

    @Column(name = "price", nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

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

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private SubscriptionPlanType type;

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

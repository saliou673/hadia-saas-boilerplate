package com.maitrisetcf.infrastructure.adapter.out.persistence.entity;

import com.maitrisetcf.domain.enumerations.DiscountType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * JPA entity mapping the {@code discount_code} table.
 */
@Entity
@Table(name = "discount_code")
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class DiscountCodeEntity extends AuditableEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 30)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountValue;

    @Column(name = "currency_code", length = 10)
    private String currencyCode;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "max_usages")
    private Integer maxUsages;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountCodeEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

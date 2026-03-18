package com.hadiasaas.infrastructure.adapter.out.persistence.entity;

import com.hadiasaas.domain.enumerations.StorageProvider;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * JPA entity mapping the {@code app_storage_settings} table.
 */
@Entity
@Table(name = "app_storage_settings")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class StorageSettingsEntity extends AuditableEntity<Long> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false, length = 50)
    private StorageProvider provider;

    @Column(name = "bucket_name")
    private String bucketName;

    @Column(name = "region")
    private String region;

    @Column(name = "endpoint")
    private String endpoint;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageSettingsEntity other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

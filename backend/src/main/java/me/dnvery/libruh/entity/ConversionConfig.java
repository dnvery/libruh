package me.dnvery.libruh.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "conversion_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversionConfig {

    @Id
    private Long id = 1L;

    @Column(name = "config_json", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String configJson;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
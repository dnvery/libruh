package me.dnvery.libruh.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "conversion_config")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getConfigJson() { return configJson; }
    public void setConfigJson(String configJson) { this.configJson = configJson; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
package me.dnvery.libruh.dto.config;

import java.time.Instant;

public class ConfigPresetResponse {
    private Long id;
    private String name;
    private Instant createdAt;

    public ConfigPresetResponse() {}

    public ConfigPresetResponse(Long id, String name, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
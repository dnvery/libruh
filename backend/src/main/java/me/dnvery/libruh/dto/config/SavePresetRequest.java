package me.dnvery.libruh.dto.config;

import jakarta.validation.constraints.NotBlank;

public class SavePresetRequest {
    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
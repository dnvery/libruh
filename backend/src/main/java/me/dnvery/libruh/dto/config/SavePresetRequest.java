package me.dnvery.libruh.dto.config;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SavePresetRequest {
    @NotBlank
    private String name;
}
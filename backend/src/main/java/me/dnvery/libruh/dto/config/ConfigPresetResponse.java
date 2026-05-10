package me.dnvery.libruh.dto.config;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigPresetResponse {
    private Long id;
    private String name;
    private Instant createdAt;
}
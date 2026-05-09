package me.dnvery.libruh.controller;

import jakarta.validation.Valid;
import me.dnvery.libruh.dto.config.ConfigPresetResponse;
import me.dnvery.libruh.dto.config.Fb2cngConfig;
import me.dnvery.libruh.dto.config.SavePresetRequest;
import me.dnvery.libruh.entity.ConfigPreset;
import me.dnvery.libruh.service.ConversionConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/config")
public class ConversionConfigController {

    private final ConversionConfigService configService;

    public ConversionConfigController(ConversionConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<Fb2cngConfig> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @PutMapping
    public ResponseEntity<Fb2cngConfig> updateConfig(@RequestBody Fb2cngConfig config) {
        return ResponseEntity.ok(configService.updateConfig(config));
    }

    @PostMapping("/reset")
    public ResponseEntity<Fb2cngConfig> resetConfig() {
        return ResponseEntity.ok(configService.resetConfig());
    }

    @GetMapping("/presets")
    public ResponseEntity<List<ConfigPresetResponse>> getAllPresets() {
        List<ConfigPresetResponse> presets = configService.getAllPresets().stream()
                .map(p -> new ConfigPresetResponse(p.getId(), p.getName(), p.getCreatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(presets);
    }

    @PostMapping("/presets")
    public ResponseEntity<ConfigPresetResponse> savePreset(@Valid @RequestBody SavePresetRequest request) {
        ConfigPreset preset = configService.savePreset(request.getName());
        return ResponseEntity.ok(new ConfigPresetResponse(preset.getId(), preset.getName(), preset.getCreatedAt()));
    }

    @PostMapping("/presets/{id}/load")
    public ResponseEntity<Fb2cngConfig> loadPreset(@PathVariable Long id) {
        return ResponseEntity.ok(configService.loadPreset(id));
    }

    @DeleteMapping("/presets/{id}")
    public ResponseEntity<Void> deletePreset(@PathVariable Long id) {
        configService.deletePreset(id);
        return ResponseEntity.noContent().build();
    }
}
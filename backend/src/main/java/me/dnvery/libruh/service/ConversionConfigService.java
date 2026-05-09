package me.dnvery.libruh.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import me.dnvery.libruh.dto.config.Fb2cngConfig;
import me.dnvery.libruh.entity.ConfigPreset;
import me.dnvery.libruh.entity.ConversionConfig;
import me.dnvery.libruh.repository.ConfigPresetRepository;
import me.dnvery.libruh.repository.ConversionConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ConversionConfigService {

    private static final Logger log = LoggerFactory.getLogger(ConversionConfigService.class);

    private final ConversionConfigRepository configRepository;
    private final ConfigPresetRepository presetRepository;
    private final ObjectMapper objectMapper;
    private final YAMLMapper yamlMapper;
    private final YAMLMapper yamlReadMapper;

    public ConversionConfigService(ConversionConfigRepository configRepository,
                                   ConfigPresetRepository presetRepository,
                                   ObjectMapper objectMapper) {
        this.configRepository = configRepository;
        this.presetRepository = presetRepository;
        this.objectMapper = objectMapper;
        this.yamlMapper = YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
                .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
                .build();
        this.yamlReadMapper = YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    public Fb2cngConfig getConfig() {
        ConversionConfig configEntity = configRepository.findById(1L).orElse(null);
        if (configEntity != null) {
            try {
                return objectMapper.readValue(configEntity.getConfigJson(), Fb2cngConfig.class);
            } catch (IOException e) {
                log.error("Failed to parse saved config, returning default", e);
            }
        }
        return loadDefaultConfig();
    }

    @Transactional
    public Fb2cngConfig updateConfig(Fb2cngConfig config) {
        try {
            String json = objectMapper.writeValueAsString(config);
            ConversionConfig configEntity = configRepository.findById(1L).orElse(null);
            if (configEntity == null) {
                configEntity = new ConversionConfig();
                configEntity.setId(1L);
            }
            configEntity.setConfigJson(json);
            configRepository.save(configEntity);
            return config;
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize config", e);
        }
    }

    @Transactional
    public Fb2cngConfig resetConfig() {
        configRepository.deleteById(1L);
        return loadDefaultConfig();
    }

    public Fb2cngConfig loadDefaultConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("default-config.yaml");
            try (InputStream is = resource.getInputStream()) {
                return yamlReadMapper.readValue(is, Fb2cngConfig.class);
            }
        } catch (IOException e) {
            log.error("Failed to load default config", e);
            return new Fb2cngConfig();
        }
    }

    public Path writeConfigToTempFile(Fb2cngConfig config) throws IOException {
        String yaml = yamlMapper.writeValueAsString(config);
        Path tempFile = Files.createTempFile("fb2cng-config-", ".yaml");
        Files.writeString(tempFile, yaml);
        return tempFile;
    }

    public List<ConfigPreset> getAllPresets() {
        return presetRepository.findAllByOrderByNameAsc();
    }

    @Transactional
    public ConfigPreset savePreset(String name) {
        Fb2cngConfig currentConfig = getConfig();
        try {
            String json = objectMapper.writeValueAsString(currentConfig);
            ConfigPreset preset = new ConfigPreset();
            preset.setName(name);
            preset.setConfigJson(json);
            return presetRepository.save(preset);
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize config for preset", e);
        }
    }

    @Transactional
    public Fb2cngConfig loadPreset(Long presetId) {
        ConfigPreset preset = presetRepository.findById(presetId)
                .orElseThrow(() -> new RuntimeException("Preset not found: " + presetId));
        try {
            Fb2cngConfig config = objectMapper.readValue(preset.getConfigJson(), Fb2cngConfig.class);
            return updateConfig(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse preset config", e);
        }
    }

    @Transactional
    public void deletePreset(Long presetId) {
        presetRepository.deleteById(presetId);
    }
}
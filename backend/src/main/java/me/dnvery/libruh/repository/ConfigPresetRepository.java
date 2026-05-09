package me.dnvery.libruh.repository;

import me.dnvery.libruh.entity.ConfigPreset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigPresetRepository extends JpaRepository<ConfigPreset, Long> {
    List<ConfigPreset> findAllByOrderByNameAsc();
}
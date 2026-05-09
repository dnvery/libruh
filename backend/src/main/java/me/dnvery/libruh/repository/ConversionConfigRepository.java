package me.dnvery.libruh.repository;

import me.dnvery.libruh.entity.ConversionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversionConfigRepository extends JpaRepository<ConversionConfig, Long> {
}
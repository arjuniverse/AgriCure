package com.agricure.repository;

import com.agricure.entity.Plant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlantRepository extends JpaRepository<Plant, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Plant> findByNameIgnoreCase(String name);
    List<Plant> findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(String name, String scientificName);
}

package com.agricure.repository;

import com.agricure.entity.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Disease> findByNameIgnoreCase(String name);
    List<Disease> findByNameContainingIgnoreCase(String name);
}

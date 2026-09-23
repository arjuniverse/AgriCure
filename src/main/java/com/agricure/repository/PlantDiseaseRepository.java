package com.agricure.repository;

import com.agricure.entity.PlantDisease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlantDiseaseRepository extends JpaRepository<PlantDisease, Long> {
    List<PlantDisease> findByPlantId(Long plantId);
    List<PlantDisease> findByDiseaseId(Long diseaseId);
    Optional<PlantDisease> findByPlantIdAndDiseaseId(Long plantId, Long diseaseId);
    boolean existsByPlantIdAndDiseaseId(Long plantId, Long diseaseId);

    @Query("select pd from PlantDisease pd join fetch pd.disease where pd.plant.id = :plantId")
    List<PlantDisease> findWithDiseaseByPlantId(@Param("plantId") Long plantId);

    @Query("select pd from PlantDisease pd join fetch pd.plant where pd.disease.id = :diseaseId")
    List<PlantDisease> findWithPlantByDiseaseId(@Param("diseaseId") Long diseaseId);
    @Query("select pd from PlantDisease pd join fetch pd.plant join fetch pd.disease")
    List<PlantDisease> findAllWithRelations();
}

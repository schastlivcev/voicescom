package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Recording;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecordingsRepository extends JpaRepository<Recording, Long> {
    Optional<Recording> findByNameAndOrder_Id(String name, Long orderId);
    List<Recording> findByOrder_Id(Long orderId);
}

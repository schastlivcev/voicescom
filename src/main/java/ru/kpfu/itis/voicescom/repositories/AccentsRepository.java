package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Accent;

import java.util.Optional;

@Repository
public interface AccentsRepository extends JpaRepository<Accent, Long> {
    Optional<Accent> findByName(String name);
}

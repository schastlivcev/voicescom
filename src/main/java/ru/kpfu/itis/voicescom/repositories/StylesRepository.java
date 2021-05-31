package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Accent;
import ru.kpfu.itis.voicescom.models.Style;

import java.util.Optional;

@Repository
public interface StylesRepository extends JpaRepository<Style, Long> {
    Optional<Style> findByName(String name);
}

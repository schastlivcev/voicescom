package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Language;

import java.util.Optional;

@Repository
public interface LanguagesRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByName(String name);
}

package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.FileInfo;

import java.util.Optional;

@Repository
public interface FileInfosRepository extends JpaRepository<FileInfo, Long> {
    Optional<FileInfo> findByPath(String path);
}

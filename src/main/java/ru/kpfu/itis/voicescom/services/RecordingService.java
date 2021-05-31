package ru.kpfu.itis.voicescom.services;

import org.springframework.core.io.FileSystemResource;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.voicescom.dto.RecordingDto;
import ru.kpfu.itis.voicescom.dto.Status;
import ru.kpfu.itis.voicescom.models.User;

import java.util.List;

public interface RecordingService {
    Status addRecording(User user, RecordingDto recordingDto, MultipartFile multipartFile);
    List<RecordingDto> findRecordings(User user, Long orderId, String serverUrl);
    FileSystemResource getRecordingFile(String fileName);
}

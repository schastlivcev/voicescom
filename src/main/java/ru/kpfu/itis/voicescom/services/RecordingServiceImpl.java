package ru.kpfu.itis.voicescom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.voicescom.dto.RecordingDto;
import ru.kpfu.itis.voicescom.dto.Status;
import ru.kpfu.itis.voicescom.handlers.FileUrlHandler;
import ru.kpfu.itis.voicescom.models.FileInfo;
import ru.kpfu.itis.voicescom.models.Order;
import ru.kpfu.itis.voicescom.models.Recording;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.repositories.FileInfosRepository;
import ru.kpfu.itis.voicescom.repositories.OrdersRepository;
import ru.kpfu.itis.voicescom.repositories.RecordingsRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RecordingServiceImpl implements RecordingService {

    @Autowired
    private RecordingsRepository recordingsRepository;

    @Autowired
    private FileInfosRepository fileInfosRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private FileUrlHandler fileUrlHandler;

    @Value("${recordings.storage-path}")
    private String STORAGE_PATH;

    @Override
    public Status addRecording(User user, RecordingDto dto, MultipartFile multipartFile) {
        Optional<Order> orderOptional = ordersRepository.findById(dto.getOrderId());
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getActor() == null || !order.getActor().getId().equals(user.getId())) {
                return Status.RECORDING_ADD_DENIED;
            }
            if(recordingsRepository.findByNameAndOrder_Id(dto.getName(), dto.getOrderId()).isPresent()) {
                return Status.RECORDING_ADD_ALREADY_EXISTS;
            }
            File storageFile = new File(STORAGE_PATH
                    + dto.getOrderId() + "_" + dto.getName().replaceAll(" ", "_")
                    + multipartFile.getOriginalFilename()
                    .substring(multipartFile.getOriginalFilename().lastIndexOf(".")));
            try {
                multipartFile.transferTo(storageFile);
            } catch (IOException e) {
                return Status.RECORDING_ADD_SAVING_ERROR;
            }
            FileInfo fileInfo = FileInfo.builder()
                    .originalName(multipartFile.getOriginalFilename())
                    .path(storageFile.getAbsolutePath())
                    .type(multipartFile.getContentType())
                    .build();
            fileInfosRepository.save(fileInfo);
            recordingsRepository.save(Recording.builder()
                    .file(fileInfo)
                    .actor(user)
                    .order(order)
                    .name(dto.getName()).build());
            return Status.RECORDING_ADD_SUCCESS;
        }
        return Status.RECORDING_ADD_ORDER_NOT_FOUND;
    }

    @Override
    public List<RecordingDto> findRecordings(User user, Long orderId, String serverUrl) {
        Optional<Order> orderOptional = ordersRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (!order.getActor().getId().equals(user.getId()) && !order.getOwner().getId().equals(user.getId())) {
                return Collections.emptyList();
            }
            List<Recording> recordings = recordingsRepository.findByOrder_Id(orderId);
            System.out.println("################## " + recordings.size());
            List<RecordingDto> recordingDtos = new ArrayList<>(recordings.size());
            for(Recording recording : recordings) {
                recordingDtos.add(toRecordingDto(recording, serverUrl));
            }
            return recordingDtos;
        }
        return Collections.emptyList();
    }

    @Override
    public FileSystemResource getRecordingFile(String fileName) {
        Optional<FileInfo> fileInfo = fileInfosRepository.findByPath(STORAGE_PATH + fileName);
        return fileInfo.map(info -> new FileSystemResource(info.getPath())).orElse(null);
    }

    private RecordingDto toRecordingDto(Recording recording, String serverUrl) {
        return RecordingDto.builder()
                .name(recording.getName())
                .actorId(recording.getActor().getId())
                .orderId(recording.getOrder().getId())
                .url(fileUrlHandler.getUrl(recording.getFile(), serverUrl)).build();
    }
}

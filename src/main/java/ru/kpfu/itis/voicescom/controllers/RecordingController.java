package ru.kpfu.itis.voicescom.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kpfu.itis.voicescom.dto.ErrorDto;
import ru.kpfu.itis.voicescom.dto.MessageDto;
import ru.kpfu.itis.voicescom.dto.RecordingDto;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.RecordingService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class RecordingController {
    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RecordingService recordingService;

    @Autowired
    private MessageSource messageSource;


    @PreAuthorize("hasAuthority('ACTOR')")
    @PostMapping("/orders/{order-id}/recordings")
    public ResponseEntity<?> uploadRecording(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @PathVariable("order-id") Long orderId,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("info") String infoJson, Locale locale) {
        try {
            RecordingDto dto = objectMapper.readValue(infoJson, RecordingDto.class);
            dto.setOrderId(orderId);
            switch (recordingService.addRecording(userDetails.getUser(), dto, file)) {
                case RECORDING_ADD_SUCCESS:
                    return ResponseEntity.ok(new MessageDto(
                            messageSource.getMessage("recording.add.success", null, locale)) );
                case RECORDING_ADD_DENIED:
                    return ResponseEntity.status(400).body(new ErrorDto(
                            messageSource.getMessage("recording.add.denied", null, locale)) );
                case RECORDING_ADD_SAVING_ERROR:
                    return ResponseEntity.status(400).body(new ErrorDto(
                            messageSource.getMessage("recording.add.saving-error", null, locale)) );
                case RECORDING_ADD_ALREADY_EXISTS:
                    return ResponseEntity.status(400).body(new ErrorDto(
                            messageSource.getMessage("recording.add.already-exists", null, locale)) );
                case RECORDING_ADD_ORDER_NOT_FOUND:
                    return ResponseEntity.status(400).body(new ErrorDto(
                            messageSource.getMessage("recording.add.order-not-found", null, locale)) );
                default:
                    return ResponseEntity.status(400).body(new ErrorDto(
                            messageSource.getMessage("recording.add.invalid-data", null, locale)) );
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Invalid json value");
        }
    }

    @PreAuthorize("hasAnyAuthority('USER','ACTOR')")
    @GetMapping("/orders/{order-id}/recordings")
    public ResponseEntity<?> getOrderRecordings(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                HttpServletRequest request,
                                                @PathVariable("order-id") Long orderId, Locale locale) {
        String serverUrl = "http://" + request.getLocalAddr() + ":" + request.getLocalPort();
        return ResponseEntity.ok(recordingService.findRecordings(userDetails.getUser(), orderId, serverUrl));
    }
}

package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.voicescom.services.RecordingService;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class FilesController {
    @Autowired
    private RecordingService recordingService;

    @PreAuthorize("permitAll()")
    @GetMapping("/files/{file-name}")
    public FileSystemResource getFile(@PathVariable("file-name") String fileName, HttpServletResponse response) throws FileNotFoundException {
        FileSystemResource file = recordingService.getRecordingFile(fileName);
        if(file == null) {
            throw new FileNotFoundException("File not found");
        }
        response.setContentType("audio/mpeg");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        return file;
    }
}
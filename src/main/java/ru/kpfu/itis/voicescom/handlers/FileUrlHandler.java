package ru.kpfu.itis.voicescom.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.voicescom.models.FileInfo;

@Component
public class FileUrlHandler {

    @Value("${spring.mvc.rest-path-prefix}")
    private String REST_PREFIX;


    public String getUrl(FileInfo fileInfo, String serverUrl) {
        return serverUrl + REST_PREFIX
                + "/files/" + fileInfo.getPath().substring(fileInfo.getPath().lastIndexOf("\\") + 1);
    }
}

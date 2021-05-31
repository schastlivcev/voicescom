package ru.kpfu.itis.voicescom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.voicescom.models.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String email;
    private User.Role role;
    private String name;
    private String surname;
    private LocalDate birthday;
    private String bio;
    private VoiceDto voice;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
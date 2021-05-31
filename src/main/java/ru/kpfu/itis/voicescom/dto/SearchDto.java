package ru.kpfu.itis.voicescom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchDto {
    @JsonProperty("all_parameters")
    private VoiceDto allParameters;
    private List<UserDto> actors;
}

package ru.kpfu.itis.voicescom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.voicescom.models.Accent;
import ru.kpfu.itis.voicescom.models.Category;
import ru.kpfu.itis.voicescom.models.Language;
import ru.kpfu.itis.voicescom.models.Style;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceDto {
    private Set<String> category;
    private Set<String> language;
    private Set<String> accent;
    private Set<String> style;
}
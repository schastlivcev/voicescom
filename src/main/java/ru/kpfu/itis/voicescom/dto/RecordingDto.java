package ru.kpfu.itis.voicescom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordingDto {
    private String name;
    @JsonProperty("actor_id")
    private Long actorId;
    @JsonProperty("order_id")
    private Long orderId;
    private String url;
}

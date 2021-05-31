package ru.kpfu.itis.voicescom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.voicescom.models.Order;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDto {
    private Long id;
    private String name;
    private String description;
    private String text;
    private VoiceDto voice;
    private UserDto owner;
    private UserDto actor;
    private Order.Status status;
}

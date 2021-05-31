package ru.kpfu.itis.voicescom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.voicescom.models.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    private String email;
    private String password;
    private String name;
    private String surname;
    private String birthday;
    private User.Role role;
}

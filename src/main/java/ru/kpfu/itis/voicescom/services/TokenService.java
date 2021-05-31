package ru.kpfu.itis.voicescom.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import ru.kpfu.itis.voicescom.models.User;

import java.util.Optional;

public interface TokenService {
    String ROLE = "role";
    String EMAIL = "email";

    String generateToken(User user);
    Optional<DecodedJWT> verifyToken(String token);
}

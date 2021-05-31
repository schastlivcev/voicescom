package ru.kpfu.itis.voicescom.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.voicescom.models.User;

import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private Algorithm algorithm;

    @Override
    public String generateToken(User user) {
        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim(EMAIL, user.getEmail())
                .withClaim(ROLE, user.getRole().name())
                .sign(algorithm);
    }

    @Override
    public Optional<DecodedJWT> verifyToken(String token) {
        DecodedJWT jwt;
        try {
            jwt = JWT
                    .require(algorithm)
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            return Optional.empty();
        }
        return Optional.ofNullable(jwt);
    }
}

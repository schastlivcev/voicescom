package ru.kpfu.itis.voicescom.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.repositories.UsersRepository;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.TokenService;

import java.util.Optional;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthentication jwtAuthentication = (JwtAuthentication) authentication;

        Optional<DecodedJWT> decodedJWTOptional = tokenService.verifyToken(jwtAuthentication.getName());
        if(decodedJWTOptional.isEmpty()) {
            throw new BadCredentialsException("Bad token: invalid token");
        }
        DecodedJWT jwt = decodedJWTOptional.get();

        Optional<User> userOptional;
        try {
            userOptional = usersRepository.findById(Long.parseLong(jwt.getSubject()));
        } catch (NumberFormatException e) {
            throw new BadCredentialsException("Bad token: invalid user id");
        }

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(!user.getRole().toString().equals(jwt.getClaim(TokenService.ROLE).asString()) ||
                    !user.getEmail().equals(jwt.getClaim(TokenService.EMAIL).asString())) {
                throw new BadCredentialsException("Bad token: modified user data");
            }
            jwtAuthentication.setAuthenticated(true);
            jwtAuthentication.setUserDetails(new UserDetailsImpl(userOptional.get()));
        }
        return jwtAuthentication;
//        throw new BadCredentialsException("Bad token: user not found");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.equals(authentication);
    }
}

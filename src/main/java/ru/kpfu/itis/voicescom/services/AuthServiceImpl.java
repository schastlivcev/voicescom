package ru.kpfu.itis.voicescom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.voicescom.dto.SignInDto;
import ru.kpfu.itis.voicescom.dto.SignUpDto;
import ru.kpfu.itis.voicescom.dto.Status;
import ru.kpfu.itis.voicescom.dto.TokenDto;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.repositories.UsersRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    @Qualifier("dateFormatter")
    private DateTimeFormatter dateFormatter;

    @Override
    public TokenDto signIn(SignInDto signInDto) {
        Optional<User> userOptional = usersRepository.findByEmail(signInDto.getEmail());
        if(userOptional.isPresent()) {
            User user = userOptional.get();
            if(passwordEncoder.matches(signInDto.getPassword(), user.getPassword())) {
                String token = tokenService.generateToken(user);
                return new TokenDto(token, Status.SIGN_IN_SUCCESS);
            }
        }
        return TokenDto.builder().status(Status.SIGN_IN_INVALID_DATA).build();
    }

    // TODO choose Status enum or exceptions?
    @Override
    public Status signUp(SignUpDto signUpDto) {
        if(signUpDto.getName() == null || signUpDto.getSurname() == null || signUpDto.getEmail() == null
                || signUpDto.getPassword() == null || signUpDto.getBirthday() == null) {
            return Status.SIGN_UP_INVALID_DATA;
        }
        if(usersRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            return Status.SIGN_UP_USER_ALREADY_EXISTS;
        }
        try {
            usersRepository.save(User.builder()
                    .email(signUpDto.getEmail())
                    .password(passwordEncoder.encode(signUpDto.getPassword()))
                    .name(signUpDto.getName())
                    .surname(signUpDto.getSurname())
                    .birthday(LocalDate.parse(signUpDto.getBirthday(), dateFormatter))
                    .role(signUpDto.getRole() == null ? User.Role.USER : signUpDto.getRole()).build());
            return Status.SIGN_UP_SUCCESS;
        } catch (DateTimeParseException e) {
            return Status.SIGN_UP_INVALID_DATE_FORMAT;
        }
    }
}

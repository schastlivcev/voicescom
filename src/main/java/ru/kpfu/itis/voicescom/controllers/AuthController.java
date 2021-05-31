package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.voicescom.dto.*;
import ru.kpfu.itis.voicescom.services.AuthService;

import java.util.Locale;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class AuthController {
    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AuthService authService;

    @PreAuthorize("permitAll()")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto, Locale locale) {
        TokenDto tokenDto = authService.signIn(signInDto);
        switch (tokenDto.getStatus()) {
            case SIGN_IN_SUCCESS:
                return ResponseEntity.ok(tokenDto);
            case SIGN_IN_USER_NOT_VERIFIED:
                return ResponseEntity.status(401).body(new ErrorDto(
                        messageSource.getMessage("sign.in.user-not-verified", null, locale) ));
            default:
                return ResponseEntity.status(401).body(new ErrorDto(
                        messageSource.getMessage("sign.in.invalid-data", null, locale) ));
        }
    }

    @PreAuthorize("permitAll()")
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto, Locale locale) {
        switch (authService.signUp(signUpDto)) {
            case SIGN_UP_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("sign.up.success", null, locale) ));
            case SIGN_UP_USER_ALREADY_EXISTS:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("sign.up.user-already-exists", null, locale) ));
            case SIGN_UP_INVALID_DATE_FORMAT:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("sign.up.invalid-date-format", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("sign.up.invalid-data", null, locale) ));
        }
    }
}

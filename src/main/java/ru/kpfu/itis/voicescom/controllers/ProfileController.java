package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.voicescom.dto.ErrorDto;
import ru.kpfu.itis.voicescom.dto.UserDto;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.ProfileService;

import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("permitAll()")
    @GetMapping("/users/{user-id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("user-id") Long userId, Locale locale) {
        Optional<UserDto> userDtoOptional = profileService.findUser(userId);
        if(userDtoOptional.isPresent()) {
            return ResponseEntity.ok(userDtoOptional.get());
        }
        return ResponseEntity.status(400).body(new ErrorDto(
                messageSource.getMessage("account.get-info.error", null, locale) ));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetailsImpl userDetails, Locale locale) {
        return getUserInfo(userDetails.getUser().getId(), locale);
    }
}

package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.voicescom.dto.ErrorDto;
import ru.kpfu.itis.voicescom.dto.SearchDto;
import ru.kpfu.itis.voicescom.dto.UserDto;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.AccountService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("permitAll()")
    @GetMapping("/users/{user-id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("user-id") Long userId, Locale locale) {
        Optional<UserDto> userDtoOptional = accountService.findUser(userId);
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

    @PreAuthorize("permitAll()")
    @GetMapping("/users/search")
    public ResponseEntity<SearchDto> searchActors(@RequestParam(name = "accent", required = false) String accent,
                                                  @RequestParam(name = "category", required = false) String category,
                                                  @RequestParam(name = "language", required = false) String language,
                                                  @RequestParam(name = "style", required = false) String style,
                                                  Locale locale) {
        Set<String> categories = category != null ? Arrays.stream(category.split(",")).collect(Collectors.toSet()) : null;
        Set<String> accents = accent != null ? Arrays.stream(accent.split(",")).collect(Collectors.toSet()) : null;
        Set<String> languages = language != null ? Arrays.stream(language.split(",")).collect(Collectors.toSet()) : null;
        Set<String> styles = style != null ? Arrays.stream(style.split(",")).collect(Collectors.toSet()) : null;
        return ResponseEntity.ok(accountService.findActors(categories, styles, languages, accents));
    }
}

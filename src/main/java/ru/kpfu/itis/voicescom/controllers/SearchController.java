package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kpfu.itis.voicescom.dto.SearchDto;
import ru.kpfu.itis.voicescom.models.Order;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.ProfileService;
import ru.kpfu.itis.voicescom.services.OrderService;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class SearchController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private OrderService orderService;

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
        return ResponseEntity.ok(profileService.findActors(categories, styles, languages, accents));
    }

    @PreAuthorize("hasAnyAuthority('ACTOR','MOD')")
    @GetMapping("/orders/search")
    public ResponseEntity<?> searchOrders(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                          @RequestParam(name = "status", required = false) String status,
                                          @RequestParam(name = "accent", required = false) String accent,
                                          @RequestParam(name = "category", required = false) String category,
                                          @RequestParam(name = "language", required = false) String language,
                                          @RequestParam(name = "style", required = false) String style,
                                          Locale locale) {
        Set<String> categories = category != null ? Arrays.stream(category.split(",")).collect(Collectors.toSet()) : null;
        Set<String> accents = accent != null ? Arrays.stream(accent.split(",")).collect(Collectors.toSet()) : null;
        Set<String> languages = language != null ? Arrays.stream(language.split(",")).collect(Collectors.toSet()) : null;
        Set<String> styles = style != null ? Arrays.stream(style.split(",")).collect(Collectors.toSet()) : null;
        Order.Status orderStatus = Order.Status.OPENED;
        if(userDetails.getUser().getRole().equals(User.Role.MOD)) {
            if(status == null) {
                orderStatus = Order.Status.VERIFYING;
            } else
                orderStatus = Order.Status.valueOf(status.toUpperCase());
        }
        return ResponseEntity.ok(
                orderService.findOrders(orderStatus, categories, styles, languages, accents));
    }
}

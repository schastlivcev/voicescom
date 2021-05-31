package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.voicescom.dto.ErrorDto;
import ru.kpfu.itis.voicescom.dto.MessageDto;
import ru.kpfu.itis.voicescom.dto.OrderDto;
import ru.kpfu.itis.voicescom.models.Order;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.OrderService;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/orders")
    public ResponseEntity<?> addOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @RequestBody OrderDto order, Locale locale) {
        switch (orderService.addOrder(order, userDetails.getUser())) {
            case ORDER_ADD_ALREADY_EXISTS:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.add.order-already-exists", null, locale) ));
            case ORDER_ADD_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("order.add.success", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.add.invalid-data", null, locale) ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                       @RequestParam(name = "status", required = false) String status, Locale locale) {
        return ResponseEntity.ok(
                orderService.findOrders(userDetails.getUser(),
                        status != null ? Order.Status.valueOf(status.toUpperCase()) : null));
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
        System.out.println("############################ " + orderStatus);
        return ResponseEntity.ok(
                orderService.findOrders(orderStatus, categories, styles, languages, accents));
    }

    @PreAuthorize("hasAuthority('MOD')")
    @PostMapping({"/orders/{order-id}/verify", "/orders/{order-id}/unverify"})
    public ResponseEntity<?> verifyOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                         @PathVariable("order-id") Long orderId,
                                         HttpServletRequest request, Locale locale) {

        boolean verify = true;
        if(request.getServletPath().contains("unverify")) {
            verify = false;
        }
//        if(verified != null && verified.equals("false")) {
//            verify = false;
//        }
        switch (orderService.verifyOrder(orderId, verify)) {
            case ORDER_VERIFY_ORDER_NOT_FOUND:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.verify.order-not-found", null, locale) ));
            case ORDER_VERIFY_VERIFY_SUCCESS:
                return ResponseEntity.ok((new MessageDto(
                        messageSource.getMessage("order.verify.verify-success", null, locale) )) );
            case ORDER_VERIFY_UNVERIFY_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("order.verify.unverify-success", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.verify.invalid-data", null, locale) ));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orders/{order-id}")
    public ResponseEntity<?> getOrder(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                      @PathVariable("order-id") Long orderId, Locale locale) {
        Optional<OrderDto> orderDtoOptional = orderService.findOrder(orderId);
        if(orderDtoOptional.isPresent()) {
            OrderDto orderDto = orderDtoOptional.get();
            if(userDetails.getUser().getRole().equals(User.Role.USER)) {
                if(!orderDto.getOwner().getId().equals(userDetails.getUser().getId())) {
                    throw new AccessDeniedException("Access denied");
                }
            } else if(userDetails.getUser().getRole().equals(User.Role.ACTOR)) {
                if(!orderDto.getActor().getId().equals(userDetails.getUser().getId())
                        || !orderDto.getStatus().equals(Order.Status.OPENED)) {
                    throw new AccessDeniedException("Access denied");
                }
            }
            return ResponseEntity.ok(orderDtoOptional.get());
        }
        return ResponseEntity.status(400).body(new ErrorDto(
                messageSource.getMessage("order.get-info.error", null, locale) ));
    }

    @PreAuthorize("hasAuthority('ACTOR')")
    @PostMapping("/orders/{order-id}/voiced")
    public ResponseEntity<?> setOrderVoiced(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable("order-id") Long orderId, Locale locale) {
        switch (orderService.voiceOrder(userDetails.getUser(), orderId)) {
            case ORDER_VOICE_DENIED:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.voice.denied", null, locale) ));
            case ORDER_VOICE_ORDER_NOT_FOUND:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.voice.order-not-found", null, locale) ));
            case ORDER_VOICE_ZERO_RECORDINGS:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.voice.zero-recordings", null, locale) ));
            case ORDER_VOICE_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("order.voice.success", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("order.voice.error", null, locale) ));
        }
    }
}

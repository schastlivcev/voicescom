package ru.kpfu.itis.voicescom.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.voicescom.dto.ErrorDto;
import ru.kpfu.itis.voicescom.dto.MessageDto;
import ru.kpfu.itis.voicescom.security.details.UserDetailsImpl;
import ru.kpfu.itis.voicescom.services.OrderService;

import java.util.Locale;

@RestController
@RequestMapping("${spring.mvc.rest-path-prefix}")
public class ProposalController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageSource messageSource;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/proposals")
    public ResponseEntity<?> getProposals(@AuthenticationPrincipal UserDetailsImpl userDetails, Locale locale) {
        return ResponseEntity.ok(orderService.findProposals(userDetails.getUser()));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/orders/{order-id}/proposals")
    public ResponseEntity<?> getOrderProposals(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                               @PathVariable("order-id") Long orderId, Locale locale) {
        return ResponseEntity.ok(orderService.findProposals(userDetails.getUser(), orderId));
    }

    @PreAuthorize("hasAuthority('ACTOR')")
    @PostMapping("/orders/{order-id}/proposals")
    public ResponseEntity<?> propose(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                     @PathVariable("order-id") Long orderId, Locale locale) {
        switch (orderService.addProposal(orderId, userDetails.getUser())) {
            case PROPOSAL_ADD_ALREADY_EXISTS:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.add.already-exists", null, locale)) );
            case PROPOSAL_ADD_ORDER_NOT_FOUND:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.add.order-not-found", null, locale)) );
            case PROPOSAL_ADD_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("proposal.add.success", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.add.invalid-data", null, locale)) );
        }
    }

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/proposals/{proposal-id}/accept")
    public ResponseEntity<?> acceptProposal(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                            @PathVariable("proposal-id") Long proposalId, Locale locale) {
        switch (orderService.acceptProposal(userDetails.getUser(), proposalId)) {
            case PROPOSAL_UPDATE_DENIED:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.update.denied", null, locale)) );
            case PROPOSAL_UPDATE_PROPOSAL_NOT_FOUND:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.update.proposal-not-found", null, locale)) );
            case PROPOSAL_UPDATE_SUCCESS:
                return ResponseEntity.ok(new MessageDto(
                        messageSource.getMessage("proposal.update.success", null, locale) ));
            default:
                return ResponseEntity.status(400).body(new ErrorDto(
                        messageSource.getMessage("proposal.update.invalid-data", null, locale)) );
        }
    }
}

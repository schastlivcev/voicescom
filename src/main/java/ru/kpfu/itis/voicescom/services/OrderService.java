package ru.kpfu.itis.voicescom.services;

import ru.kpfu.itis.voicescom.dto.OrderDto;
import ru.kpfu.itis.voicescom.dto.ProposalDto;
import ru.kpfu.itis.voicescom.dto.Status;
import ru.kpfu.itis.voicescom.models.Order;
import ru.kpfu.itis.voicescom.models.Proposal;
import ru.kpfu.itis.voicescom.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface OrderService {
    Status addOrder(OrderDto order, User user);
    List<OrderDto> findOrders(User user, Order.Status status);
    List<OrderDto> findOrders(Order.Status status,
                              Set<String> categories, Set<String> styles, Set<String> languages, Set<String> accents);
    Optional<OrderDto> findOrder(Long orderId);

    Status verifyOrder(Long orderId, boolean verified);
    Status voiceOrder(User user, Long orderId);

    Status addProposal(Long orderId, User user);
    Optional<ProposalDto> findProposal(Long proposalId);
    List<ProposalDto> findProposals(User user);
    List<ProposalDto> findProposals(User user, Long orderId);
    Status acceptProposal(User user, Long proposalId);
}
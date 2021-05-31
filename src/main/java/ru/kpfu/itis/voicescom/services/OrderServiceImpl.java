package ru.kpfu.itis.voicescom.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.voicescom.dto.*;
import ru.kpfu.itis.voicescom.handlers.VoiceDtoHandler;
import ru.kpfu.itis.voicescom.models.Order;
import ru.kpfu.itis.voicescom.models.Proposal;
import ru.kpfu.itis.voicescom.models.User;
import ru.kpfu.itis.voicescom.repositories.OrdersRepository;
import ru.kpfu.itis.voicescom.repositories.ProposalsRepository;
import ru.kpfu.itis.voicescom.repositories.RecordingsRepository;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ProposalsRepository proposalsRepository;

    @Autowired
    private RecordingsRepository recordingsRepository;

    @Autowired
    private VoiceDtoHandler voiceDtoHandler;

    @Override
    public Status addOrder(OrderDto order, User user) {
        if(ordersRepository.findByOwner_IdAndName(user.getId(), order.getName()).isPresent()) {
            return Status.ORDER_ADD_ALREADY_EXISTS;
        }
        if(order.getName() == null || order.getText() == null) {
            return Status.ORDER_ADD_INVALID_DATA;
        }
        ordersRepository.save(Order.builder()
                .name(order.getName())
                .description(order.getDescription())
                .text(order.getText())
                .owner(user)
                .voice(order.getVoice() != null ? voiceDtoHandler.toVoice(order.getVoice()) : null).build());
        return Status.ORDER_ADD_SUCCESS;
    }

    @Override
    public List<OrderDto> findOrders(User user, Order.Status status) {
        List<Order> orders;
        if(status != null) {
            orders = ordersRepository.findByUserIdAndStatus(user.getId(), status);
        } else {
            orders = ordersRepository.findByUserId(user.getId());
        }
        return toOrderDtos(orders);
    }

    @Override
    public List<OrderDto> findOrders(Order.Status status,
                                     Set<String> categories, Set<String> styles,
                                     Set<String> languages, Set<String> accents) {
        List<Order> allOrders = ordersRepository.findByStatus(status != null ? status : Order.Status.OPENED);
        List<Order> rightOrders = new ArrayList<>();
        if(categories != null || styles != null || languages != null || accents != null) {
            for(Order order : allOrders) {
                if(order.getVoice() != null) {
                    VoiceDto voice = voiceDtoHandler.toVoiceDto(order.getVoice());
                    boolean contains = false;
                    if(categories != null && !categories.isEmpty()
                            && (!Collections.disjoint(voice.getCategory(), categories) || voice.getCategory().isEmpty()))
                        contains = true;
                    if(accents != null && !accents.isEmpty()
                            && (!Collections.disjoint(voice.getAccent(), accents) || voice.getAccent().isEmpty()))
                        contains = true;
                    if(styles != null && !styles.isEmpty()
                            && (!Collections.disjoint(voice.getStyle(), styles) || voice.getStyle().isEmpty()))
                        contains = true;
                    if(languages != null && !languages.isEmpty()
                            && (!Collections.disjoint(voice.getLanguage(), languages) || voice.getLanguage().isEmpty()))
                        contains = true;
                    if(contains)
                        rightOrders.add(order);
                } else {
                    rightOrders.add(order);
                }
            }
        } else {
            rightOrders = allOrders;
        }
        return toOrderDtos(rightOrders);
    }

    @Override
    public Optional<OrderDto> findOrder(Long orderId) {
        Optional<Order> order = ordersRepository.findById(orderId);
        return order.map(this::toOrderDto);
    }

    @Override
    public Status verifyOrder(Long orderId, boolean verified) {
        Optional<Order> orderOptional = ordersRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if(verified) {
                order.setStatus(Order.Status.OPENED);
            } else
                order.setStatus(Order.Status.NOT_VERIFIED);
            ordersRepository.save(order);
            if(verified) {
                return Status.ORDER_VERIFY_VERIFY_SUCCESS;
            } else
                return Status.ORDER_VERIFY_UNVERIFY_SUCCESS;
        }
        return Status.ORDER_VERIFY_ORDER_NOT_FOUND;
    }

    @Override
    public Status voiceOrder(User user, Long orderId) {
        Optional<Order> orderOptional = ordersRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if (order.getActor() == null || !order.getActor().getId().equals(user.getId())) {
                return Status.ORDER_VOICE_DENIED;
            }
            if(order.getStatus().equals(Order.Status.VOICING)) {
                if(recordingsRepository.findByOrder_Id(orderId).size() == 0) {
                    return Status.ORDER_VOICE_ZERO_RECORDINGS;
                }
                order.setStatus(Order.Status.VOICED);
                ordersRepository.save(order);
                return Status.ORDER_VOICE_SUCCESS;
            }
            return Status.ORDER_VOICE_ERROR;
        }
        return Status.ORDER_VOICE_ORDER_NOT_FOUND;
    }

    @Override
    public Status addProposal(Long orderId, User user) {
        if(proposalsRepository.findByOrder_IdAndProposer_Id(orderId, user.getId()).isPresent()) {
            return Status.PROPOSAL_ADD_ALREADY_EXISTS;
        }
        Optional<Order> orderOptional = ordersRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            proposalsRepository.save(Proposal.builder()
                    .proposer(user)
                    .order(orderOptional.get())
                    .status(Proposal.Status.PROPOSED).build());
            return Status.PROPOSAL_ADD_SUCCESS;
        }
        return Status.PROPOSAL_ADD_ORDER_NOT_FOUND;
    }

    @Override
    public Optional<ProposalDto> findProposal(Long proposalId) {
        Optional<Proposal> proposalOptional = proposalsRepository.findById(proposalId);
        return proposalOptional.map(this::toProposalDto);
    }

    @Override
    public List<ProposalDto> findProposals(User user) {
        List<Proposal> proposals = proposalsRepository.findByUserId(user.getId());
        List<ProposalDto> proposalDtos = new ArrayList<>(proposals.size());
        for(Proposal proposal : proposals) {
            proposalDtos.add(toProposalDto(proposal));
        }
        return proposalDtos;
    }

    @Override
    public List<ProposalDto> findProposals(User user, Long orderId) {
        Optional<Order> orderOptional = ordersRepository.findById(orderId);
        if(orderOptional.isPresent()) {
            Order order = orderOptional.get();
            if(!order.getOwner().getId().equals(user.getId()))
                return Collections.emptyList();
        }
        List<Proposal> proposals = proposalsRepository.findByOrder_Id(orderId);
        List<ProposalDto> proposalDtos = new ArrayList<>(proposals.size());
        for(Proposal proposal : proposals) {
            proposalDtos.add(toProposalDto(proposal));
        }
        return proposalDtos;
    }

    @Override
    public Status acceptProposal(User user, Long proposalId) {
        Optional<Proposal> proposalOptional = proposalsRepository.findById(proposalId);
        if(proposalOptional.isPresent()) {
            Proposal proposal = proposalOptional.get();
            if(!proposal.getOrder().getOwner().getId().equals(user.getId())) {
                return Status.PROPOSAL_UPDATE_DENIED;
            }
            List<Proposal> proposals = proposalsRepository.findByOrder_Id(proposal.getOrder().getId());
            for(Proposal prop : proposals) {
                if(prop.getId().equals(proposalId)) {
                    prop.setStatus(Proposal.Status.ACCEPTED);
                } else {
                    prop.setStatus(Proposal.Status.DECLINED);
                }
            }
            proposalsRepository.saveAll(proposals);
            Optional<Order> orderOptional = ordersRepository.findById(proposal.getOrder().getId());
            if(orderOptional.isEmpty()) {
                return Status.PROPOSAL_UPDATE_INVALID_DATA;
            }
            Order order = orderOptional.get();
            order.setActor(proposal.getProposer());
            order.setStatus(Order.Status.VOICING);
            ordersRepository.save(order);
            return Status.PROPOSAL_UPDATE_SUCCESS;
        }
        return Status.PROPOSAL_UPDATE_PROPOSAL_NOT_FOUND;
    }

    private ProposalDto toProposalDto(Proposal proposal) {
        return ProposalDto.builder()
                .id(proposal.getId())
                .orderId(proposal.getOrder().getId())
                .proposerId(proposal.getProposer().getId())
                .status(proposal.getStatus()).build();
    }

    private List<OrderDto> toOrderDtos(List<Order> orders) {
        List<OrderDto> orderDtos = new ArrayList<>(orders.size());
        for(Order order : orders) {
            orderDtos.add(toOrderDto(order));
        }
        return orderDtos;
    }

    private OrderDto toOrderDto(Order order) {
        return OrderDto.builder()
                .id(order.getId())
                .name(order.getName())
                .actor(order.getActor() != null
                        ? UserDto.builder()
                        .id(order.getActor().getId())
                        .email(order.getActor().getEmail())
                        .name(order.getActor().getName())
                        .surname(order.getActor().getSurname())
                        .role(order.getActor().getRole())
                        .birthday(order.getActor().getBirthday())
                        .createdAt(order.getActor().getCreatedAt())
                        .bio(order.getActor().getBio())
                        .voice(order.getActor().getVoice() != null
                                ? voiceDtoHandler.toVoiceDto(order.getActor().getVoice())
                                : null).build()
                        : null)
                .owner(UserDto.builder()
                        .id(order.getOwner().getId())
                        .email(order.getOwner().getEmail())
                        .name(order.getOwner().getName())
                        .surname(order.getOwner().getSurname())
                        .role(order.getOwner().getRole())
                        .birthday(order.getOwner().getBirthday())
                        .createdAt(order.getOwner().getCreatedAt())
                        .bio(order.getOwner().getBio())
                        .voice(order.getOwner().getVoice() != null
                                ? voiceDtoHandler.toVoiceDto(order.getOwner().getVoice())
                                : null).build())
                .description(order.getDescription())
                .status(order.getStatus())
                .text(order.getText())
                .voice(order.getVoice() != null
                        ? voiceDtoHandler.toVoiceDto(order.getVoice())
                        : null).build();
    }
}

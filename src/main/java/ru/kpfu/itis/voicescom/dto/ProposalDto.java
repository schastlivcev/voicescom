package ru.kpfu.itis.voicescom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kpfu.itis.voicescom.models.Proposal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalDto {
    private Long id;
    private Proposal.Status status;
    @JsonProperty("order_id")
    private Long orderId;
    @JsonProperty("proposer_id")
    private Long proposerId;
}
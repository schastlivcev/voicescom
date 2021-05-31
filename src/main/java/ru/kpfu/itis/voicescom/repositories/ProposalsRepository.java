package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Proposal;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalsRepository extends JpaRepository<Proposal, Long> {
    @Query("SELECT p FROM Proposal p WHERE p.order.owner.id = :userId OR p.proposer.id = :userId")
    List<Proposal> findByUserId(Long userId);
    Optional<Proposal> findByOrder_IdAndProposer_Id(Long orderId, Long proposerId);
    List<Proposal> findByOrder_Id(Long orderId);
}

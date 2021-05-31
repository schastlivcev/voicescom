package ru.kpfu.itis.voicescom.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.voicescom.models.Order;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOwner_IdAndName(Long ownerId, String name);
    @Query("SELECT o FROM Order o WHERE (o.owner.id = :userId OR o.actor.id = :userId) AND o.status = :status")
    List<Order> findByUserIdAndStatus(Long userId, Order.Status status);
    @Query("SELECT o FROM Order o WHERE o.owner.id = :userId OR o.actor.id = :userId")
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(Order.Status status);
}

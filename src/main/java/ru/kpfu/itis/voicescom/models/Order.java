package ru.kpfu.itis.voicescom.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "actor_id", nullable = true)
    private User actor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    @PrePersist
    private void defaultStatus() {
        if(status == null) {
            status = Status.VERIFYING;
        }
    }

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String text;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "voice_id", nullable = true)
    private Voice voice;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public enum Status {
        VERIFYING,
        NOT_VERIFIED,
        OPENED,
        VOICING,
        VOICED,
        COMPLETED
    }
}
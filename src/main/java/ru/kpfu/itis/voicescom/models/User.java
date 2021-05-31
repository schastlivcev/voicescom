package ru.kpfu.itis.voicescom.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(length = 60, nullable = false)
    private String surname;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate birthday;

    @Column
    private String bio;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "voice_id", nullable = true)
    private Voice voice;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public enum Role {
        USER,
        ACTOR,
        MOD,
        ADMIN
    }
}
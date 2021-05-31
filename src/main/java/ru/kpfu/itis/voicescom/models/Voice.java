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
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "voices")
public class Voice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "voice_languages",
            joinColumns = @JoinColumn(name = "voice_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id"))
    private Set<Language> languages;

    @ManyToMany
    @JoinTable(name = "voice_accents",
            joinColumns = @JoinColumn(name = "voice_id"),
            inverseJoinColumns = @JoinColumn(name = "accent_id"))
    private Set<Accent> accents;

    @ManyToMany
    @JoinTable(name = "voice_categories",
            joinColumns = @JoinColumn(name = "voice_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories;

    @ManyToMany
    @JoinTable(name = "voice_styles",
            joinColumns = @JoinColumn(name = "voice_id"),
            inverseJoinColumns = @JoinColumn(name = "style_id"))
    private Set<Style> styles;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    @ColumnDefault("CURRENT_TIMESTAMP")
    @CreationTimestamp
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
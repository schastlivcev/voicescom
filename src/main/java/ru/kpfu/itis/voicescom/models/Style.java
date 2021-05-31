package ru.kpfu.itis.voicescom.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "styles")
public class Style {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "styles")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Voice> voices;
}
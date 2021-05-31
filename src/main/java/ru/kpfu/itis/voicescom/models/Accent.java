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
@Table(name = "accents")
public class Accent {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "accents")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Voice> voices;
}
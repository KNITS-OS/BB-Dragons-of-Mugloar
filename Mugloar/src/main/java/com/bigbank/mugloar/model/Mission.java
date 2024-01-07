package com.bigbank.mugloar.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String externalId;
    private String gameExternalId;
    private String message;
    private int reward;
    private int expiresIn;
    private String encrypted;
    private String probability;

    @Enumerated(EnumType.STRING)
    private MissionOutcomeType outcome;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "game_id")
    private Game game;
}

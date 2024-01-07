package com.bigbank.mugloar.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MissionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean success;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private String message;

    @Enumerated(EnumType.STRING)
    private MissionResultType resultType;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "mission_id")
    private Mission mission;

}

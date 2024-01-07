package com.bigbank.mugloar.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class PurchasedItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shoppingSuccess;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private int level;
    private LocalDateTime time;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "game_id")
    private Game game;
}

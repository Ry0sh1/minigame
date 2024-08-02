package com.dt.minigame.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Heal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @ManyToOne
    private Game game;
    private boolean active;
    private int cooldown;

    public Heal(Game game, boolean active, int cooldown) {
        this.game = game;
        this.active = active;
        this.cooldown = cooldown;
    }
}

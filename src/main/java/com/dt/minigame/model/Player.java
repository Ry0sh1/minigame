package com.dt.minigame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    private String username;
    private double x;
    private double y;
    @ManyToOne
    @JoinColumn(name = "game_code", nullable = false)
    private Game game;
    private int killCounter;
    private int deathCounter;
    private int hp;
    private String weapon;

    @Override
    public String toString() {
        return "{\"username\":\"" + username + "\"," +
                " \"x\":\"" + x + "\"," +
                " \"y\":\"" + y + "\"," +
                " \"weapon\":\"" + weapon + "\"}";
    }
}

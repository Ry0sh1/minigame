package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    private String username;
    private double x;
    private double y;
    private Game game;
    private int killCounter;
    private int deathCounter;
    private int hp;
    private int shield;
    private String weapon;
    private boolean alive;
    private int respawnTimer;

    @Override
    public String toString() {
        return "{\"username\":\"" + username + "\"," +
                " \"x\":\"" + x + "\"," +
                " \"y\":\"" + y + "\"," +
                " \"weapon\":\"" + weapon + "\"," +
                " \"killCounter\":\"" + killCounter + "\"," +
                " \"deathCounter\":\"" + deathCounter + "\"}";
    }
}

package de.ryoshi.minigame.model;

import de.ryoshi.minigame.util.Constant;
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
    private double width;
    private double height;
    private Input input;
    private int killCounter;
    private int deathCounter;
    private int hp;
    private int shield;
    private Weapon weapon;
    private boolean alive;
    private boolean isReloading;
    private int currentReloadFrame;
    private int respawnTimer;
    private double angle;
    private String gameCode;
    private PowerUp currentPowerUp;

    public Player(String username, String gameCode) {
        this.gameCode = gameCode;
        this.username = username;
        this.killCounter = 0;
        this.deathCounter = 0;
        this.hp = Constant.MAX_HP;
        this.alive = true;
        this.isReloading = false;
        this.input = new Input();
        this.currentReloadFrame = 0;
        this.width = Constant.PLAYER_WIDTH;
        this.height = Constant.PLAYER_HEIGHT;
        this.currentPowerUp = null;
    }

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

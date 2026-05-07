package de.ryoshi.minigame.model;

import de.ryoshi.minigame.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Bomb extends AbstractGameObject {
    private String player;
    private int timer;
    private double radius;
    private int damage;
    private int bombAfterLifeTimer;
    private boolean exploded;

    public Bomb(String player, double x, double y) {
        super(x, y);
        timer = Constant.BOMB_TIMER;
        radius = Constant.BOMB_RADIUS;
        damage = Constant.BOMB_DAMAGE;
        if (player.equals("server")) {
            radius = radius / 2;
            damage = damage / 2;
        }
        bombAfterLifeTimer = Constant.BOMB_AFTER_LIFE_TIMER;
        exploded = false;
    }
}


package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bullet extends AbstractGameObject {
    private double angle;
    private double speed;
    private double distance;
    private double range;
    private double radius;
    private String playerID;
    private int damage;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\", \"x\":" + x + ", \"y\":" + y + ", \"angle\":" + angle + ", \"speed\":\"" + speed + "\"}";
    }
}

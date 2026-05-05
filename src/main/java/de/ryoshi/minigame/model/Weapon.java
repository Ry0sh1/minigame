package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Weapon {
    private String name;
    private int speed;
    private int range;
    private int reloadFrames;
    private int damage;
    private int bulletRadius;
    private int sprayRadius;
    private int bulletCount;
}

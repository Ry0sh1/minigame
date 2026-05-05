package de.ryoshi.minigame.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Input {
    boolean up;
    boolean down;
    boolean right;
    boolean left;
    boolean shoot;

    public Input() {
        up = false;
        down = false;
        right = false;
        left = false;
        shoot = false;
    }
}

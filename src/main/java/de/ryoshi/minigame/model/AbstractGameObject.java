package de.ryoshi.minigame.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AbstractGameObject {
    protected int id;
    protected double x;
    protected double y;

    public AbstractGameObject() {
    }

    public AbstractGameObject(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

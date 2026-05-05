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

    public AbstractGameObject(double y, double x) {
        this.y = y;
        this.x = x;
    }
}

package de.ryoshi.minigame.model;

import de.ryoshi.minigame.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShootingPoint extends AbstractGameObject {
    private int max;
    private int currentFrame;

    public ShootingPoint(double x, double y) {
        super(x, y);
        this.currentFrame = 0;
        this.max = Constant.MINIMAP_SHOOTING_POINT_TIME;
    }
}

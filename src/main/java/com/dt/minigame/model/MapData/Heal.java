package com.dt.minigame.model.MapData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Heal {

    private int id;
    private boolean active;
    private int cooldown;
    private double x;
    private double y;
    private String code;

    public Heal(double x, double y, boolean active, int cooldown, String code) {
        this.active = active;
        this.cooldown = cooldown;
        this.code = code;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Heal{" +
                "id=" + id +
                ", active=" + active +
                ", cooldown=" + cooldown +
                ", x=" + x +
                ", y=" + y +
                ", code='" + code + '\'' +
                '}';
    }
}

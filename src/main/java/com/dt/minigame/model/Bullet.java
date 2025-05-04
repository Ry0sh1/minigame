package com.dt.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bullet {
    private int id;
    private double x;
    private double y;
    private double angle;
    private double speed;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\", \"x\":" + x + ", \"y\":" + y + ", \"angle\":" + angle + ", \"speed\":\"" + speed + "\"}";
    }
}

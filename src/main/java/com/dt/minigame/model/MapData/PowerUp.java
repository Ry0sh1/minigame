package com.dt.minigame.model.MapData;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PowerUp {

    private int id;
    private String name;
    private double x;
    private double y;
    private String code;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\", " +
                "\"name\":\"" + name + "\", " +
                "\"x\":\"" + x + "\", " +
                "\"y\":\"" + y + "\"}";
    }
}

package com.dt.minigame.model.MapData;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Heal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
}

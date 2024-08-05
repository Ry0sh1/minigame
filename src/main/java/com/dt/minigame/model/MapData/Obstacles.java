package com.dt.minigame.model.MapData;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Obstacles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private double x;
    private double y;
    private int width;
    private int height;
    private int hp;
    private String code;

    public Obstacles(double x, double y, int width, int height, int hp) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hp = hp;
    }

    public Obstacles(String code, com.dt.minigame.util.map.Obstacles obstacles, int hp){
        this.x = obstacles.getX();
        this.y = obstacles.getY();
        this.width = obstacles.getWidth();
        this.height = obstacles.getHeight();
        this.code = code;
        this.hp = hp;
    }

}
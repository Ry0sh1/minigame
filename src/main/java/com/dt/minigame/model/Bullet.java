package com.dt.minigame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Bullet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private int x;
    private int y;
    private double angle;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\", \"x\":" + x + ", \"y\":" + y + ", \"angle\":" + angle + "}";
    }
}

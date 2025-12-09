package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Obstacle extends AbstractGameObject {
    private double x;
    private double y;
    private int width;
    private int height;
}
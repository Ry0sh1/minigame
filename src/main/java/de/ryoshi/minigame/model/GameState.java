package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GameState {
    private List<Player> players;
    private List<Bullet> bullets;
    private List<PowerUp> powerUps;
    private List<Heal> heals;
    private List<Bomb> bombs;
    private List<Obstacle> obstacles;
    private String currentEvent;
}

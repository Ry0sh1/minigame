package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RawMapData {

    private String name;
    private int player;
    private int width;
    private int height;
    private List<Obstacle> obstacles;
    private List<Position> spawnPoints;
    private List<Position> powerUpSpawn;
    private List<Position> healPadSpawn;

}

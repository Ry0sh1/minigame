package de.ryoshi.minigame.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MapData {

    private String name;
    private int width;
    private int height;
    private List<Heal> healPads;
    private List<Obstacle> obstacles;
    private List<PowerUp> powerUps;

    public MapData(RawMapData rawMapData){
        this.width = rawMapData.getWidth();
        this.height = rawMapData.getHeight();
        this.name = rawMapData.getName();
    }

}

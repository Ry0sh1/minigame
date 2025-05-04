package com.dt.minigame.model.MapData;

import com.dt.minigame.util.map.RawMapData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MapData {

    private String code;
    private String name;
    private int width;
    private int height;
    private List<Heal> heal_pads;
    private List<Obstacles> obstacles;
    private List<PowerUp> power_ups;

    public MapData(RawMapData rawMapData){
        this.width = rawMapData.getWidth();
        this.height = rawMapData.getHeight();
        this.name = rawMapData.getName();
    }

}

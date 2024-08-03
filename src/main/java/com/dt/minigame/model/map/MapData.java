package com.dt.minigame.model.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapData {

    private String name;
    private int player;
    private int width;
    private int height;
    private List<Obstacles> obstacles;
    private List<Point> spawn_points;
    private List<Point> power_up_spawn;
    private List<Point> heal_pad_spawn;

}

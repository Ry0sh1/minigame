package com.dt.minigame.model.MapData;

import com.dt.minigame.util.map.RawMapData;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MapData {

    @Id
    private String code;
    private String name;
    private int width;
    private int height;
    @OneToMany
    private List<Heal> heal_pads;
    @OneToMany
    private List<Obstacles> obstacles;
    @OneToMany
    private List<PowerUp> power_ups;

    public MapData(RawMapData rawMapData){
        this.width = rawMapData.getWidth();
        this.height = rawMapData.getHeight();
        this.name = rawMapData.getName();
    }

}

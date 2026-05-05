package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameData {
    private GameState gameState;
    private MapData mapData;
    private List<Weapon> weaponList;
}

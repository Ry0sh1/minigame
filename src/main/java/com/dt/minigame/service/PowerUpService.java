package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.MapData.MapData;
import com.dt.minigame.repository.MapDataRepository;
import com.dt.minigame.repository.PowerUpRepository;
import com.dt.minigame.util.map.RawMapData;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PowerUpService {

    private final RawMapService rawMapService;
    private final MapDataRepository mapDataRepository;
    private final PowerUpRepository powerUpRepository;

    public PowerUpService(RawMapService rawMapService, MapDataRepository mapDataRepository, PowerUpRepository powerUpRepository) {
        this.rawMapService = rawMapService;
        this.mapDataRepository = mapDataRepository;
        this.powerUpRepository = powerUpRepository;
    }

    public void spawnPowerUp(Game game) throws IOException {
        RawMapData rawMapData = rawMapService.convertJsonToMap(rawMapService.loadMapByName(game.getMap()));
        MapData mapData = mapDataRepository.findById(game.getCode()).orElseThrow();

    }

}

package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.stores.GameStore;
import com.dt.minigame.stores.HealStore;
import com.dt.minigame.stores.MapDataStore;
import com.dt.minigame.stores.ObstacleStore;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameStore gameStore;
    private final HealStore healStore;
    private final MapDataStore mapDataStore;
    private final ObstacleStore obstacleStore;
    private final PowerUpService powerUpService;

    public GameService(GameStore gameStore,
                       HealStore healStore,
                       MapDataStore mapDataStore,
                       ObstacleStore obstacleStore,
                       PowerUpService powerUpService) {
        this.gameStore = gameStore;
        this.healStore = healStore;
        this.mapDataStore = mapDataStore;
        this.obstacleStore = obstacleStore;
        this.powerUpService = powerUpService;
    }

    public void deleteGame(String code){
        gameStore.deleteByCode(code);
        mapDataStore.deleteById(code);
        healStore.deleteAllByCode(code);
        powerUpService.deleteAllByGame(code);
        obstacleStore.deleteAllByCode(code);
    }

    public Game findByCode(String code) {
        return gameStore.findById(code);
    }
}

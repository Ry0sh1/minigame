package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final HealRepository healRepository;
    private final MapDataRepository mapDataRepository;
    private final ObstacleRepository obstacleRepository;
    private final PowerUpService powerUpService;

    public GameService(GameRepository gameRepository,
                       HealRepository healRepository,
                       MapDataRepository mapDataRepository,
                       ObstacleRepository obstacleRepository,
                       PowerUpService powerUpService) {
        this.gameRepository = gameRepository;
        this.healRepository = healRepository;
        this.mapDataRepository = mapDataRepository;
        this.obstacleRepository = obstacleRepository;
        this.powerUpService = powerUpService;
    }

    @Transactional
    public void deleteGame(String code){
        gameRepository.deleteByCode(code);
        mapDataRepository.deleteById(code);
        healRepository.deleteAllByCode(code);
        powerUpService.deleteAllByGame(code);
        obstacleRepository.deleteAllByCode(code);
    }

    public Game findByCode(String code) {
        return gameRepository.findById(code).orElseThrow();
    }
}

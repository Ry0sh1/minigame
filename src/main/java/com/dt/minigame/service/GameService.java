package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.HealRepository;
import com.dt.minigame.repository.MapDataRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final HealRepository healRepository;
    private final MapDataRepository mapDataRepository;

    public GameService(GameRepository gameRepository, HealRepository healRepository, MapDataRepository mapDataRepository) {
        this.gameRepository = gameRepository;
        this.healRepository = healRepository;
        this.mapDataRepository = mapDataRepository;
    }

    public void deleteGame(String code){
        Game game = gameRepository.findById(code).orElseThrow();
        healRepository.deleteAll(mapDataRepository.findById(code).orElseThrow().getHeal_pads());
        gameRepository.delete(game);
    }

    public Game findByCode(String code) {
        return gameRepository.findById(code).orElseThrow();
    }
}

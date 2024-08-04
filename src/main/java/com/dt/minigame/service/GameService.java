package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.HealRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final HealRepository healRepository;

    public GameService(GameRepository gameRepository, HealRepository healRepository) {
        this.gameRepository = gameRepository;
        this.healRepository = healRepository;
    }

    public void deleteGame(String code){
        Game game = gameRepository.findById(code).orElseThrow();
        healRepository.deleteAll(healRepository.findAllByGame(game));
        gameRepository.delete(game);
    }

    public Game findByCode(String code) {
        return gameRepository.findById(code).orElseThrow();
    }
}

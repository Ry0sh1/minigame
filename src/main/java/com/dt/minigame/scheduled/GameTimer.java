package com.dt.minigame.scheduled;

import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.service.EventService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GameTimer {


    private final GameRepository gameRepository;
    private final EventService eventService;

    public GameTimer(GameRepository gameRepository, EventService eventService) {
        this.gameRepository = gameRepository;
        this.eventService = eventService;
    }

    @Scheduled(fixedRate = 1000)
    public void updateGameTimer(){
        gameRepository.findAll().forEach(game -> {
            game.setTime(game.getTime()+1);
            if (game.getTime() % 120 == 0){ //Alle 2 Minuten spawnt ein Event
                try {
                    eventService.sendEventMessage(game);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (game.getTime() >= 60*8){
                //TODO: Stop Game
            }
            gameRepository.save(game);
        });
    }

}

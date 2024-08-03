package com.dt.minigame.scheduled;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.model.Player;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import com.dt.minigame.service.EventService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GameTimer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GameRepository gameRepository;
    private final EventService eventService;
    private final PlayerRepository playerRepository;

    public GameTimer(SimpMessageSendingOperations messagingTemplate, GameRepository gameRepository, EventService eventService, PlayerRepository playerRepository) {
        this.messagingTemplate = messagingTemplate;
        this.gameRepository = gameRepository;
        this.eventService = eventService;
        this.playerRepository = playerRepository;
    }

    @Scheduled(fixedRate = 1000)
    public void updateGameTimer(){
        gameRepository.findAll().forEach(game -> {
            if (game.isRunning()){
                game.setTime(game.getTime()+1);
                if (game.getTime() % 120 == 0){ //Alle 2 Minuten spawnt ein Event
                    try {
                        eventService.sendEventMessage(game);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (game.getTime() >= 60*8){
                    stopGame(game);
                }
                gameRepository.save(game);
            }
        });
    }

    public void stopGame(Game game){
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setType(MessageType.END_GAME);
        List<Player> players = new ArrayList<>(playerRepository.findAllByGame(game));
        Player max = players.get(0);
        for (int i = 1; i < players.size(); i++){
            if (players.get(i).getKillCounter() > max.getKillCounter()){
                max = players.get(i);
            }
        }
        message.setContent(max.getUsername());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

}

package com.dt.minigame.scheduled;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.model.Player;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import com.dt.minigame.service.EventService;
import com.dt.minigame.service.HealService;
import com.dt.minigame.service.PowerUpService;
import com.dt.minigame.service.RawMapService;
import com.dt.minigame.util.Constant;
import com.dt.minigame.util.map.Point;
import com.dt.minigame.util.map.RawMapData;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class GameTimer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GameRepository gameRepository;
    private final EventService eventService;
    private final PlayerRepository playerRepository;
    private final RawMapService rawMapService;
    private final HealService healService;
    private final PowerUpService powerUpService;

    public GameTimer(SimpMessageSendingOperations messagingTemplate, GameRepository gameRepository, EventService eventService, PlayerRepository playerRepository, RawMapService rawMapService, HealService healService, PowerUpService powerUpService) {
        this.messagingTemplate = messagingTemplate;
        this.gameRepository = gameRepository;
        this.eventService = eventService;
        this.playerRepository = playerRepository;
        this.rawMapService = rawMapService;
        this.healService = healService;
        this.powerUpService = powerUpService;
    }

    @Scheduled(fixedRate = 1000)
    public void updateGameTimer(){
        healService.findAll().forEach(heal -> {
            if (!heal.isActive()){
                heal.setCooldown(heal.getCooldown() - 1);
                if (heal.getCooldown() <= 0){
                    heal.setActive(true);
                    healService.reactivateHeal(heal);
                }
                healService.save(heal);
            }
        });
        gameRepository.findAll().forEach(game -> {
            if (game.isRunning()){
                game.setTime(game.getTime()+1);
                if (game.getTime() % Constant.EVENT_INTERVAL == 0){
                    try {
                        eventService.sendEventMessage(game);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (game.getTime() % Constant.POWERUP_INTERVAL == 0){
                    try {
                        powerUpService.spawnPowerUp(game);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (game.getTime() >= Constant.GAME_TIME){
                    stopGame(game);
                }
                if (game.isEvent()){
                    game.setCurrentEventTime(game.getCurrentEventTime() - 1);
                    if (game.getCurrentEventTime() <= 0){
                        eventService.stopEvent(game);
                    }
                }
                gameRepository.save(game);
                sendTimer(game);
            }
        });
        playerRepository.findAll().forEach(player -> {
            if (!player.isAlive()){
                player.setRespawnTimer(player.getRespawnTimer() - 1);
                if (player.getRespawnTimer() <= 0){
                    player.setAlive(true);
                    respawnPlayer(player);
                }
                playerRepository.save(player);
            }
        });
    }

    private void respawnPlayer(Player player) {
        try {
            playerSetRandomSpawnPoint(player, rawMapService);
            //TODO: player.setWeapon(message.getContent());
            Message message = new Message();
            message.setContent(player.toString());
            message.setPlayer(player.getUsername());
            message.setCode(player.getGame().getCode());
            message.setType(MessageType.SPAWN);
            messagingTemplate.convertAndSend("/start-game/game/"+player.getGame().getCode(),message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void playerSetRandomSpawnPoint(Player player, RawMapService rawMapService) throws IOException {
        RawMapData mapData = rawMapService.convertJsonToMap(rawMapService.loadMapByName(player.getGame().getMap()));
        Random random = new Random();
        int n = random.nextInt(mapData.getSpawn_points().size());
        Point spawn = mapData.getSpawn_points().get(n);
        player.setX(spawn.getX());
        player.setY(spawn.getY());
    }

    public void stopGame(Game game){
        game.setRunning(false);
        gameRepository.save(game);
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
        List<Player> winner = new ArrayList<>();
        for (Player player : players) {
            if (max.getKillCounter() == player.getKillCounter() && max.getDeathCounter() == player.getDeathCounter()) {
                winner.add(player);
            }
        }
        StringBuilder winnerList = new StringBuilder(winner.get(0).getUsername());
        for (int i = 1; i < winner.size(); i++){
            winnerList.append(",").append(winner.get(i));
        }
        message.setContent(winnerList.toString());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

    public void sendTimer(Game game){
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setType(MessageType.TIMER);
        message.setContent("" + game.getTime());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

}

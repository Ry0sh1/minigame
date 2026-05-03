package de.ryoshi.minigame.scheduled;

import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.MessageType;
import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.service.EventService;
import de.ryoshi.minigame.service.GameService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
import de.ryoshi.minigame.model.Position;
import de.ryoshi.minigame.model.RawMapData;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@AllArgsConstructor
public class GameTimer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GameStore gameStore;
    private final GameService gameService;
    private final EventService eventService;
    private final PlayerStore playerStore;

    @Scheduled(fixedRate = 1000)
    public void updateGameTimer(){
        gameStore.findAll().forEach(game -> {
            if (game.isRunning()) {
                game.setTime(game.getTime() + 1);
                gameService.tickHeals(game);
                if (game.getTime() % Constant.EVENT_INTERVAL == 0){
                    eventService.sendEventMessage(game);
                }
                if (game.getTime() % Constant.POWERUP_INTERVAL == 0){
                    gameService.spawnPowerUp(game);
                }
                if (game.getTime() >= Constant.GAME_TIME){
                    stopGame(game);
                }
                if (game.isEvent()){
                    eventService.handleEvent(game);
                }
                sendTimer(game);
            }
        });
        playerStore.findAll().forEach(player -> {
            if (!player.isAlive()){
                player.setRespawnTimer(player.getRespawnTimer() - 1);
                if (player.getRespawnTimer() <= 0){
                    player.setAlive(true);
                    respawnPlayer(player);
                }
            }
        });
    }

    private void respawnPlayer(Player player) {
        playerSetRandomSpawnPoint(player);
        //TODO: player.setWeapon(message.getContent());
        Message message = new Message();
        message.setContent(player.toString());
        message.setPlayer(player.getUsername());
        message.setCode(player.getGame().getCode());
        message.setType(MessageType.SPAWN);
        messagingTemplate.convertAndSend("/start-game/game/" + player.getGame().getCode(),message);
    }

    public static void playerSetRandomSpawnPoint(Player player) {
        RawMapData mapData = player.getGame().getRawMapData();
        Random random = new Random();
        int n = random.nextInt(mapData.getSpawnPoints().size());
        Position spawn = mapData.getSpawnPoints().get(n);
        player.setX(spawn.x());
        player.setY(spawn.y());
    }

    public void stopGame(Game game){
        game.setRunning(false);
        gameStore.save(game);
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setType(MessageType.END_GAME);
        List<Player> players = new ArrayList<>(playerStore.findAllByGame(game));
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

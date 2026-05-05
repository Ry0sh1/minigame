package de.ryoshi.minigame.service;

import de.ryoshi.minigame.model.*;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.FileUtil;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class GameService {

    private final GameStore gameStore;
    private final PlayerStore playerStore;
    private final SimpMessageSendingOperations messagingTemplate;
    private final FileUtil fileUtil;
    private final WeaponService weaponService;

    public void deleteGame(String code){
        gameStore.deleteByCode(code);
    }

    public Game findByCode(String code) {
        return gameStore.findById(code);
    }

    public GameData getGameData(String code) {
        Game game = gameStore.findById(code);
        GameData gameData = new GameData();
        gameData.setGameState(game.getState(playerStore.findAllByGame(game)));
        gameData.setWeaponList(weaponService.loadAllWeapons());
        gameData.setMapData(game.getMapData());
        return gameData;
    }

    public void tickHeals(Game game){
        game.getHeals().forEach(heal -> {
            if (heal.tick()) {
                Message message = new Message();
                message.setPlayer("server");
                message.setCode(game.getCode());
                message.setType(MessageType.REACTIVATE_HEAL);
                message.setContent(String.valueOf(heal.getId()));
                messagingTemplate.convertAndSend("/start-game/game/" + game.getCode(),message);
            }
        });
    }

    public void spawnPowerUp(Game game) {
        RawMapData rawMapData = game.getRawMapData();
        MapData mapData = game.getMapData();
        List<PowerUp> powerUpList = mapData.getPowerUps();
        if (rawMapData.getPowerUpSpawn().size() <= powerUpList.size()){
            //Erstelle kein neues Powerup
            return;
        }
        List<Position> possibleSpawnPoints = calculatePossibleSpawnPoints(rawMapData, mapData);
        PowerUp powerUp = new PowerUp();
        Random random = new Random();
        int n = random.nextInt(possibleSpawnPoints.size());
        Position powerUpSpawn = possibleSpawnPoints.get(n);
        powerUp.setX(powerUpSpawn.getX());
        powerUp.setY(powerUpSpawn.getY());
        powerUp.setCode(game.getCode());
        try {
            powerUp.setName(fileUtil.convertJsonToJustName(fileUtil.getRandomJSONFromDirectory("classpath:assets/powerups")).getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        powerUpList.add(powerUp);
        mapData.setPowerUps(powerUpList);
        game.powerUpSpawn(powerUp);
        sendPowerUpSpawnMessage(powerUp);
    }

    private List<Position> calculatePossibleSpawnPoints(RawMapData rawMapData, MapData mapData){
        ArrayList<Position> possibleSpawnPoints = new ArrayList<>();
        List<Position> allPowerUpSpawnPoints = rawMapData.getPowerUpSpawn();
        List<PowerUp> currentPowerUps = mapData.getPowerUps();
        for (Position position : allPowerUpSpawnPoints){
            boolean possible = true;
            for (PowerUp powerUp : currentPowerUps){
                if (position.getX() == powerUp.getX() && position.getY() == powerUp.getY()) {
                    possible = false;
                    break;
                }
            }
            if (possible) {
                possibleSpawnPoints.add(position);
            }
        }
        return possibleSpawnPoints;
    }

    private void sendPowerUpSpawnMessage(PowerUp powerUp) {
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(powerUp.getCode());
        message.setType(MessageType.SPAWN_POWERUP);
        message.setContent(powerUp.toString());
        messagingTemplate.convertAndSend("/start-game/game/"+powerUp.getCode(),message);
    }

}

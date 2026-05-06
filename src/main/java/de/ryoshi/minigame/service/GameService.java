package de.ryoshi.minigame.service;

import de.ryoshi.minigame.model.*;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
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
        message.setType(MessageType.SPAWN_POWERUP);
        message.setContent(powerUp.toString());
        messagingTemplate.convertAndSend("/start-game/game/"+powerUp.getCode(),message);
    }

    public void usePowerUp(Game game, Player player) {
        if (player.getCurrentPowerUp() == null) return;
        switch (player.getCurrentPowerUp().getName()) {
            case "shield" -> {
                if (player.getShield() + Constant.SHIELD_AMOUNT <= 100){
                    player.setShield(player.getShield() + Constant.SHIELD_AMOUNT);
                }
            }
            case "flash" -> {
                player.setX((player.getX() + Math.cos(player.getAngle()) * Constant.FLASH_DISTANCE));
                player.setY((player.getY() + Math.sin(player.getAngle()) * Constant.FLASH_DISTANCE));
            }
            case "laser-gun" -> {
                //TODO
            }
            case "bomb" -> {
                game.createBomb(player.getUsername(), player.getX(), player.getY());
            }
        }
        player.setCurrentPowerUp(null);
    }

    public void processPlayerHit(Player killer, Player shotPlayer, int damage) {
        if ((shotPlayer.getHp() + shotPlayer.getShield()) - damage <= 0) {
            if (killer != null && !killer.getUsername().equals(shotPlayer.getUsername())){
                killer.setKillCounter(killer.getKillCounter() + 1);
            }
            shotPlayer.setDeathCounter(shotPlayer.getDeathCounter() + 1);
            shotPlayer.setAlive(false);
            shotPlayer.setX(0);
            shotPlayer.setY(0);
            shotPlayer.setHp(Constant.MAX_HP);
            shotPlayer.setRespawnTimer(Constant.RESPAWN_TIMER);
        } else {
            int rest = shotPlayer.getShield() - damage;
            shotPlayer.setShield(shotPlayer.getShield() - damage);
            if (shotPlayer.getShield() < 0){
                shotPlayer.setHp(shotPlayer.getHp() - Math.abs(rest));
                shotPlayer.setShield(0);
            }
        }
    }

}

package de.ryoshi.minigame.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ryoshi.minigame.model.*;
import de.ryoshi.minigame.service.EventService;
import de.ryoshi.minigame.service.GameService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GameTimer {

    private final SimpMessageSendingOperations messagingTemplate;
    private final GameStore gameStore;
    private final GameService gameService;
    private final EventService eventService;
    private final PlayerStore playerStore;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedRate = 1000)
    public void updateGameTimer(){
        gameStore.findAll().forEach(game -> {
            if (game.isRunning()) {
                game.setTime(game.getTime() + 1);
                gameService.tickHeals(game);
                if (game.getTime() % Constant.EVENT_INTERVAL == 0){
                    eventService.startEvent(game);
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
                    respawnPlayer(player);
                }
            }
        });
    }

    @Scheduled(fixedRate = 16)
    public void gameLoop() {
        gameStore.findAll().forEach(game -> {
            if (!game.isRunning()) return;

            synchronized (game) {
                updateBombs(game);
                updatePlayer(game);
                updateBullets(game);
                try {
                    sendGameState(game);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void updateBombs(Game game) {
        for (Bomb bomb : new ArrayList<>(game.getBombStore().getAll())) {
            bomb.setTimer(bomb.getTimer() - 1);
            if (bomb.getTimer() <= 0 && !bomb.isExploded()) {
                bomb.setExploded(true);

                playerStore.findAllByGame(game).forEach(player -> {
                    if (playerInBombRadius(player, bomb)) {
                        gameService.processPlayerHit(null, player, bomb.getDamage());
                    }
                });

                bomb.setTimer(bomb.getBombAfterLifeTimer());
            } else if (bomb.getTimer() <= 0){
                game.getBombStore().delete(bomb.getId());
            }
        }
    }

    private boolean playerInBombRadius(Player player, Bomb bomb) {
        double pCenterX = player.getX() + player.getWidth() / 2;
        double pCenterY = player.getY() + player.getHeight() / 2;

        double dx = pCenterX - bomb.getX();
        double dy = pCenterY - bomb.getY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        return distance <= bomb.getRadius();
    }

    private void updateBullets(Game game) {
        for (Bullet bullet : new ArrayList<>(game.getBulletStore().getAll())) {
            double x = bullet.getX();
            double y = bullet.getY();
            bullet.setX(bullet.getX() + Math.cos(bullet.getAngle()) * bullet.getSpeed());
            bullet.setY(bullet.getY() + Math.sin(bullet.getAngle()) * bullet.getSpeed());
            bullet.setDistance(bullet.getDistance() + (Math.abs(bullet.getX() - x) + Math.abs(bullet.getY() - y)));

            if (bullet.getDistance() >= bullet.getRange()) {
                game.getBulletStore().delete(bullet.getId());
                continue;
            }
            if (bulletCollapsingIntoWall(bullet, game)) {
                game.getBulletStore().delete(bullet.getId());
                continue;
            }
            if (bulletCollapsingWithPlayer(bullet, game)) {
                game.getBulletStore().delete(bullet.getId());
            }
        }
    }

    private boolean bulletCollapsingIntoWall(Bullet bullet, Game game){
        if (bullet.getX() >= game.getMapData().getWidth() ||
                bullet.getY() >= game.getMapData().getHeight() ||
                bullet.getX() <= 0 || bullet.getY() <= 0){
            return true;
        }

        for (Obstacle obstacle : game.getObstacleStore().getAll()) {
            if (bullet.getX() + bullet.getRadius() >= obstacle.getX() &&
                    bullet.getX() <= obstacle.getX() + obstacle.getWidth() &&
                    bullet.getY() + bullet.getRadius() >= obstacle.getY() &&
                    bullet.getY() <= obstacle.getY() + obstacle.getHeight()){
                return true;
            }
        }

        return false;
    }

    private boolean bulletCollapsingWithPlayer(Bullet bullet, Game game) {
        for (Player player : playerStore.findAllByGame(game)) {
            if (!player.isAlive() || player.getUsername().equals(bullet.getPlayerID())) continue;
            if (bullet.getX() + bullet.getRadius() >= player.getX() &&
                    bullet.getX() <= player.getX() + player.getWidth() &&
                    bullet.getY() + bullet.getRadius() >= player.getY() &&
                    bullet.getY() <= player.getY() + player.getHeight()){

                Player killer = playerStore.findById(bullet.getPlayerID());

                gameService.processPlayerHit(killer, player, bullet.getDamage());

                return true;
            }
        }
        return false;
    }

    private void updatePlayer(Game game) {
        for (Player player : playerStore.findAllByGame(game)) {
            if (!player.isAlive()) continue;

            Input input = player.getInput();

            if (player.isReloading()) {
                player.setCurrentReloadFrame(player.getCurrentReloadFrame() + 1);
                if (player.getCurrentReloadFrame() % player.getWeapon().getReloadFrames() == 0){
                    player.setCurrentReloadFrame(0);
                    player.setReloading(false);
                }

            }

            if (player.getInput().isShoot() && !player.isReloading()) {
                double centerX = (player.getX() + player.getWidth() / 2.0);
                double centerY  = (player.getY() + player.getHeight() / 2.0);

                double spawnDistance = Math.max(player.getWidth(), player.getHeight()) / 2.0 + player.getWeapon().getBulletRadius();

                double bulletSpawnX = centerX + spawnDistance * Math.cos(player.getAngle());
                double bulletSpawnY = centerY + spawnDistance * Math.sin(player.getAngle());

                double maxSpread = Math.toRadians(player.getWeapon().getSprayRadius());

                for (int i = 0; i < player.getWeapon().getBulletCount(); i++) {
                    double randomOffset = (Math.random() * 2 - 1) * maxSpread;
                    double bulAngle = player.getAngle() + randomOffset;

                    Bullet bullet = new Bullet();

                    bullet.setX(bulletSpawnX);
                    bullet.setY(bulletSpawnY);
                    bullet.setAngle(bulAngle);
                    bullet.setPlayerID(player.getUsername());
                    bullet.setSpeed(player.getWeapon().getSpeed());
                    bullet.setRange(player.getWeapon().getRange());
                    bullet.setRadius(player.getWeapon().getBulletRadius());
                    bullet.setDamage(player.getWeapon().getDamage());
                    game.shootBullet(bullet);

                    player.setReloading(true);
                    player.setCurrentReloadFrame(0);
                }
            }

            Position pos = new Position(player.getX(), player.getY());

            if (input.isUp() && pos.getY() - Constant.PLAYER_SPEED > 0) {
                pos.setY(pos.getY() - Constant.PLAYER_SPEED);
            }
            if (input.isDown() && pos.getY() + Constant.PLAYER_SPEED < game.getMapData().getHeight()) {
                pos.setY(pos.getY() + Constant.PLAYER_SPEED);
            }
            if (input.isRight() && pos.getX() + Constant.PLAYER_SPEED < game.getMapData().getWidth()) {
                pos.setX(pos.getX() + Constant.PLAYER_SPEED);
            }
            if (input.isLeft() && pos.getX() - Constant.PLAYER_SPEED > 0) {
                pos.setX(pos.getX() - Constant.PLAYER_SPEED);
            }

            game.getObstacleStore().getAll().forEach(obstacle -> {
                if (pos.getX() + player.getWidth() > obstacle.getX() && pos.getX() < obstacle.getX() &&
                        pos.getY() + player.getHeight() > obstacle.getY() && pos.getY() < obstacle.getY() + obstacle.getHeight()) {
                    pos.setX(obstacle.getX() - player.getWidth());
                }

                if (pos.getX() < obstacle.getX() + obstacle.getWidth() && pos.getX() + player.getWidth() > obstacle.getX() + obstacle.getWidth() &&
                        pos.getY() + player.getHeight() > obstacle.getY() && pos.getY() < obstacle.getY() + obstacle.getHeight()) {
                    pos.setX(obstacle.getX() + obstacle.getWidth());
                }

                if (pos.getY() + player.getHeight() > obstacle.getY() && pos.getY() < obstacle.getY() &&
                        pos.getX() + player.getWidth() > obstacle.getX() && pos.getX() < obstacle.getX() + obstacle.getWidth()) {
                    pos.setY(obstacle.getY() - player.getHeight());
                }

                if (pos.getY() < obstacle.getY() + obstacle.getHeight() && pos.getY() + player.getHeight() > obstacle.getY() + obstacle.getHeight() &&
                        pos.getX() +player.getWidth() > obstacle.getX() && pos.getX() < obstacle.getX() + obstacle.getWidth()) {
                    pos.setY(obstacle.getY() + obstacle.getHeight());
                }
            });

            player.setX(pos.getX());
            player.setY(pos.getY());

            game.getHeals().forEach(heal -> {
                if (isColliding(player, heal)) {
                    game.healUsed(heal.getId());
                    player.setHp(Math.min(player.getHp() + Constant.HEAL, Constant.MAX_HP));
                }
            });
            game.getPowerUpStore().getAll().forEach(powerUp -> {
                if (isColliding(player, powerUp)){
                    player.setCurrentPowerUp(powerUp);
                    game.powerUpPicked(powerUp.getId());
                }
            });
        }
    }

    private boolean isColliding(Player p, AbstractGameObject o) {
        return  p.getX() >= o.getX() && p.getX() <= o.getX() + Constant.POWERUP_PICKUP_WIDTH && p.getY() >= o.getY() && p.getY() <= o.getY() + Constant.POWERUP_PICKUP_HEIGHT ||
                p.getX() >= o.getX() && p.getX() <= o.getX() + Constant.POWERUP_PICKUP_WIDTH && p.getY() + p.getHeight() >= o.getY() && p.getY() + p.getHeight() <= o.getY() + Constant.POWERUP_PICKUP_HEIGHT ||
                p.getX() + p.getWidth() >= o.getX() && p.getX() + p.getWidth() <= o.getX() + Constant.POWERUP_PICKUP_WIDTH && p.getY() + p.getHeight() >= o.getY() && p.getY() + p.getHeight() <= o.getY() + Constant.POWERUP_PICKUP_HEIGHT ||
                p.getX() + p.getWidth() >= o.getX() && p.getX() + p.getWidth() <= o.getX() + Constant.POWERUP_PICKUP_WIDTH && p.getY() >= o.getY() && p.getY() <= o.getY() + Constant.POWERUP_PICKUP_HEIGHT;
    }

    private void respawnPlayer(Player player) {
        Position position = gameStore.findById(player.getGameCode()).getRandomSpawnPoint();
        player.setX(position.getX());
        player.setY(position.getY());
        player.setAlive(true);
    }

    public void stopGame(Game game){
        game.setRunning(false);
        gameStore.save(game);
        Message message = new Message();
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

    public void sendGameState(Game game) throws JsonProcessingException {
        GameStateMessage message = new GameStateMessage();
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setType(MessageType.GAME_STATE);
        message.setContent(game.getState(playerStore.findAllByGame(game)));
        messagingTemplate.convertAndSend("/start-game/game/" + game.getCode(), message);
    }

    public void sendTimer(Game game){
        Message message = new Message();
        message.setType(MessageType.TIMER);
        message.setContent("" + game.getTime());
        messagingTemplate.convertAndSend("/start-game/game/" + game.getCode(), message);
    }

}

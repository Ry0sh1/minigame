package de.ryoshi.minigame.model;

import de.ryoshi.minigame.stores.*;
import de.ryoshi.minigame.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Game {

    private String code;
    private int time;
    private boolean running;
    private String currentEvent;
    private boolean isEvent;
    private int currentEventTime;
    private final RawMapData rawMapData;
    private final MapData mapData;
    private final BulletStore bulletStore = new BulletStore();
    private final HealStore healStore = new HealStore();
    private final PowerUpStore powerUpStore = new PowerUpStore();
    private final ObstacleStore obstacleStore = new ObstacleStore();
    private final BombStore bombStore = new BombStore();
    private final ShootingPointStore shootingPointStore = new ShootingPointStore();

    public Game(RawMapData rawMapData) {
        this.rawMapData = rawMapData;
        this.mapData = new MapData(rawMapData);
        initMapData();
    }

    public void initMapData() {
        List<Heal> heals = new ArrayList<>();
        for (Position position : rawMapData.getHealPadSpawn())  {
            Heal heal = new Heal(position.getX(), position.getY(), true, 0);
            Heal heal1 = healStore.save(heal);
            heals.add(heal1);
        }
        List<Obstacle> obstacles = new ArrayList<>();
        for (Obstacle obstacle: rawMapData.getObstacles()) {
            obstacles.add(obstacleStore.save(obstacle));
        }
        mapData.setObstacles(obstacles);
        mapData.setHealPads(heals);
        mapData.setPowerUps(new ArrayList<>());
    }

    public void resetObstacles() {
        List<Obstacle> obstacles = new ArrayList<>();
        for (Obstacle obstacle: rawMapData.getObstacles()) {
            obstacles.add(obstacleStore.save(obstacle));
        }
        mapData.setObstacles(obstacles);
    }

    public Bullet shootBullet(Bullet bullet) {
        return bulletStore.save(bullet);
    }

    public void createBomb(String player, double x, double y) {
        Bomb bomb = new Bomb(player, x, y);
        bombStore.save(bomb);
    }

    public void deleteBullet(int id) {
        if (bulletStore.exists(id)) bulletStore.delete(id);
    }

    public void healUsed(int id) {
        Heal heal = healStore.find(id);
        heal.setActive(false);
        heal.setCooldown(Constant.HEAL_COOLDOWN);
    }

    public void powerUpPicked(int id) {
        if (powerUpStore.exists(id)) {
            PowerUp powerUp = powerUpStore.find(id);
            mapData.getPowerUps().remove(powerUp);
            powerUpStore.delete(id);
        }
    }

    public Collection<Heal> getHeals() {
        return healStore.getAll();
    }

    public GameState getState(List<Player> players) {
        return new GameState(
                players,
                new ArrayList<>(bulletStore.getAll()),
                new ArrayList<>(powerUpStore.getAll()),
                new ArrayList<>(healStore.getAll()),
                new ArrayList<>(bombStore.getAll()),
                new ArrayList<>(obstacleStore.getAll()),
                new ArrayList<>(shootingPointStore.getAll()),
                currentEvent);
    }

    public Position getRandomSpawnPoint() {
        Random random = new Random();
        int n = random.nextInt(rawMapData.getSpawnPoints().size());
        Position spawn = rawMapData.getSpawnPoints().get(n);
        return spawn;
    }

    public void powerUpSpawn(PowerUp powerUp) {
        powerUpStore.save(powerUp);
    }
}


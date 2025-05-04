package com.dt.minigame.stores;

import com.dt.minigame.model.MapData.Obstacles;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class ObstacleStore {

    private final HashMap<Integer, Obstacles> obstacleStore;
    private int currentId;

    public ObstacleStore() {
        this.obstacleStore = new HashMap<>();
        this.currentId = 0;
    }

    public Obstacles save(Obstacles obstacle) {
        obstacle.setId(currentId);
        obstacleStore.put(currentId, obstacle);
        currentId++;
        return obstacle;
    }

    public void deleteAllByCode(String code) {
        obstacleStore.forEach((key, obstacle) -> {
            if (obstacle.getCode().equals(code)) {
                obstacleStore.remove(key);
            }
        });
    }
}

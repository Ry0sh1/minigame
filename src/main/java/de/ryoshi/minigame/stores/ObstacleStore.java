package de.ryoshi.minigame.stores;

import de.ryoshi.minigame.model.Obstacle;

import java.util.concurrent.ConcurrentHashMap;

public class ObstacleStore extends AbstractStore<Obstacle> {

    public void clear() {
        store = new ConcurrentHashMap<>();
    }

}

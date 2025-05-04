package com.dt.minigame.stores;

import com.dt.minigame.model.MapData.PowerUp;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PowerUpStore {

    private final HashMap<Integer, PowerUp> powerUpStore;
    private int currentId;

    public PowerUpStore() {
        this.powerUpStore = new HashMap<>();
        this.currentId = 0;
    }

    public PowerUp save(PowerUp powerUp) {
        powerUp.setId(currentId);
        powerUpStore.put(currentId, powerUp);
        currentId++;
        return powerUp;
    }

    public PowerUp findById(int id) {
        return powerUpStore.get(id);
    }

    public void delete(PowerUp powerUp) {
        powerUpStore.remove(powerUp.getId());
    }

    public void deleteAllByCode(String code) {
        powerUpStore.forEach((key, powerup) -> {
            if (powerup.getCode().equals(code)) {
                powerUpStore.remove(powerup.getId());
            }
        });
    }
}

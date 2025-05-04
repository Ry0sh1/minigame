package com.dt.minigame.stores;

import com.dt.minigame.model.Bullet;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class BulletStore {

    private final HashMap<Integer, Bullet> bulletStore;
    private int currentId;

    public BulletStore() {
        this.bulletStore = new HashMap<>();
        this.currentId = 0;
    }

    public Bullet save(Bullet bullet) {
        bullet.setId(currentId);
        bulletStore.put(currentId, bullet);
        currentId++;
        return bullet;
    }

    public boolean existsById(int id) {
        return bulletStore.containsKey(id);
    }

    public void deleteById(int id) {
        bulletStore.remove(id);
    }
}

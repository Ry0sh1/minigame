package com.dt.minigame.stores;

import com.dt.minigame.model.MapData.Heal;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class HealStore {

    private final HashMap<Integer, Heal> healStore;
    private int currentId;

    public HealStore() {
        this.healStore = new HashMap<>();
        this.currentId = 0;
    }

    public Heal save(Heal heal) {
        heal.setId(currentId);
        healStore.put(currentId, heal);
        currentId++;
        return heal;
    }

    public Heal findById(int id) {
        return healStore.get(id);
    }

    public void deleteAllByCode(String code) {
        healStore.forEach((key, heal) -> {
            if (heal.getCode().equals(code)) {
                healStore.remove(key);
            }
        });
    }

    public List<Heal> findAll() {
        List<Heal> heals = new ArrayList<>();
        healStore.forEach((key, heal) -> {
            heals.add(heal);
        });
        return heals;
    }
}

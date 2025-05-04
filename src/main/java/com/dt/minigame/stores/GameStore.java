package com.dt.minigame.stores;

import com.dt.minigame.model.Game;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GameStore {

    private final HashMap<String, Game> gameStore;

    public GameStore() {
        this.gameStore = new HashMap<>();
    }

    public Game findById(String id) {
        return gameStore.get(id);
    }

    public Game save(Game game) {
        return gameStore.put(game.getCode(), game);
    }

    public boolean containsId(String id) {
        return gameStore.containsKey(id);
    }

    public List<Game> findAll() {
        ArrayList<Game> games = new ArrayList<>();
        gameStore.forEach((key, game) -> {
            games.add(game);
        });
        return games;
    }

    public void deleteByCode(String code) {
        gameStore.remove(code);
    }
}

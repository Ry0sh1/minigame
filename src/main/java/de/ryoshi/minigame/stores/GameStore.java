package de.ryoshi.minigame.stores;

import de.ryoshi.minigame.model.Game;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class GameStore {

    private final HashMap<String, Game> store;

    public GameStore() {
        this.store = new HashMap<>();
    }

    public Game findById(String id) {
        return store.get(id);
    }

    public void save(Game game) {
        store.put(game.getCode(), game);
    }

    public boolean containsId(String id) {
        return store.containsKey(id);
    }

    public List<Game> findAll() {
        ArrayList<Game> games = new ArrayList<>();
        store.forEach((key, game) -> games.add(game));
        return games;
    }

    public void deleteByCode(String code) {
        store.remove(code);
    }
}

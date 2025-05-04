package com.dt.minigame.stores;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class PlayerStore{

    private final HashMap<String, Player> playerStore;

    public PlayerStore() {
        this.playerStore = new HashMap<>();
    }

    public List<Player> findAllByGame(Game game) {
        List<Player> players = new ArrayList<>();
        playerStore.forEach((key, player) -> {
            if (player.getGame().equals(game)){
                players.add(player);
            }
        });
        return players;
    }

    public Player save(Player player) {
        return playerStore.put(player.getUsername(), player);
    }

    public Player findById(String username) {
        return playerStore.get(username);
    }

    public void deleteById(String username) {
        playerStore.remove(username);
    }

    public List<Player> findAll() {
        ArrayList<Player> players = new ArrayList<>();
        playerStore.forEach((key, player) -> {
            players.add(player);
        });
        return players;
    }

    public void saveAll(List<Player> players) {
        players.forEach(this::save);
    }
}

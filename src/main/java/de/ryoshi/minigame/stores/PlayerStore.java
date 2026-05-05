package de.ryoshi.minigame.stores;

import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Player;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerStore{

    private final ConcurrentHashMap<String, Player> playerStore;

    public PlayerStore() {
        this.playerStore = new ConcurrentHashMap<>();
    }

    public List<Player> findAllByGame(Game game) {
        List<Player> players = new ArrayList<>();
        playerStore.forEach((key, player) -> {
            if (player.getGameCode().equals(game.getCode())){
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

package com.dt.minigame.controller;

import com.dt.minigame.model.Player;
import com.dt.minigame.repository.PlayerRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final PlayerRepository playerRepository;

    public GameRestController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @GetMapping("/get-all-player")
    public List<Player> getAllPlayer(){
        List<Player> players = new ArrayList<>();
        playerRepository.findAll().forEach(players::add);
        return players;
    }

}

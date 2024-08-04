package com.dt.minigame.controller;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Player;
import com.dt.minigame.model.map.MapData;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import com.dt.minigame.service.MapService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final MapService mapService;

    public GameRestController(PlayerRepository playerRepository, GameRepository gameRepository, MapService mapService) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.mapService = mapService;
    }

    @GetMapping("/get-all-player/{code}")
    public List<Player> getAllPlayer(@PathVariable String code){
        return new ArrayList<>(playerRepository.findAllByGame(gameRepository.findById(code).orElseThrow()));
    }

    @PostMapping("/create-game")
    public String createGame(@RequestBody Game game) throws IOException {
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        do{
            for (int i = 0;i<4;i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameRepository.findById(code.toString()).isPresent());

        game = new Game();
        game.setCode(code.toString());
        game.setMap(mapService.convertJsonToMap(mapService.loadRandomMap()).getName());
        game.setTime(0);
        game.setRunning(true);
        gameRepository.save(game);
        return code.toString();
    }

    @GetMapping("/get-map-data/{code}")
    public MapData getTest(@PathVariable String code) throws IOException {
        Game game = gameRepository.findById(code).orElseThrow();
        MapData mapData = mapService.convertJsonToMap(mapService.loadMapByName(game.getMap()));
        return mapData;
    }

    @GetMapping("/get-game/{code}")
    public Game getGame(@PathVariable String code){
        return gameRepository.findById(code).orElseThrow();
    }

}

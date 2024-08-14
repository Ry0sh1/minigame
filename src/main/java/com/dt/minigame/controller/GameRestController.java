package com.dt.minigame.controller;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.model.MapData.Obstacles;
import com.dt.minigame.model.Player;
import com.dt.minigame.model.MapData.MapData;
import com.dt.minigame.repository.*;
import com.dt.minigame.service.RawMapService;
import com.dt.minigame.util.map.RawMapData;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final RawMapService rawMapService;
    private final HealRepository healRepository;
    private final MapDataRepository mapDataRepository;
    private final ObstacleRepository obstacleRepository;

    public GameRestController(PlayerRepository playerRepository, GameRepository gameRepository, RawMapService rawMapService, HealRepository healRepository, MapDataRepository mapDataRepository, ObstacleRepository obstacleRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.rawMapService = rawMapService;
        this.healRepository = healRepository;
        this.mapDataRepository = mapDataRepository;
        this.obstacleRepository = obstacleRepository;
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
        game.setMap(rawMapService.convertJsonToMap(rawMapService.loadRandomMap()).getName());
        game.setTime(0);
        game.setRunning(true);
        gameRepository.save(game);


        RawMapData rawMapData = rawMapService.convertJsonToMap(rawMapService.loadRandomMap());

        List<Heal> heals = new ArrayList<>();
        for (int i = 0; i < rawMapData.getHeal_pad_spawn().size(); i++){
            Heal heal = new Heal(
                    rawMapData.getHeal_pad_spawn().get(i).getX(),
                    rawMapData.getHeal_pad_spawn().get(i).getY(),
                    true,
                    0,
                    code.toString());
            heals.add(healRepository.save(heal));
        }
        List<Obstacles> obstacles = new ArrayList<>();
        for (int i = 0; i < rawMapData.getObstacles().size(); i++){
            Obstacles obstacle = new Obstacles(code.toString(),rawMapData.getObstacles().get(i),100);
            obstacles.add(obstacleRepository.save(obstacle));
        }

        MapData mapData = new MapData(rawMapData);
        mapData.setCode(code.toString());
        mapData.setObstacles(obstacles);
        mapData.setHeal_pads(heals);
        mapData.setPower_ups(null);
        mapDataRepository.save(mapData);


        mapDataRepository.save(mapData);

        return code.toString();
    }

    @GetMapping("/get-map-data/{code}")
    public MapData getTest(@PathVariable String code) {
        return mapDataRepository.findById(code).orElseThrow();
    }

    @GetMapping("/get-game/{code}")
    public Game getGame(@PathVariable String code){
        return gameRepository.findById(code).orElseThrow();
    }

}

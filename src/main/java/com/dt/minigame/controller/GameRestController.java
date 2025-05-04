package com.dt.minigame.controller;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.model.MapData.Obstacles;
import com.dt.minigame.model.Player;
import com.dt.minigame.model.MapData.MapData;
import com.dt.minigame.service.RawMapService;
import com.dt.minigame.stores.*;
import com.dt.minigame.util.map.RawMapData;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final PlayerStore playerStore;
    private final GameStore gameStore;
    private final RawMapService rawMapService;
    private final HealStore healStore;
    private final MapDataStore mapDataStore;
    private final ObstacleStore obstacleStore;

    public GameRestController(PlayerStore playerStore,
                              GameStore gameStore,
                              RawMapService rawMapService,
                              HealStore healStore,
                              MapDataStore mapDataStore,
                              ObstacleStore obstacleStore) {
        this.playerStore = playerStore;
        this.gameStore = gameStore;
        this.rawMapService = rawMapService;
        this.healStore = healStore;
        this.mapDataStore = mapDataStore;
        this.obstacleStore = obstacleStore;
    }

    @GetMapping("/get-all-player/{code}")
    public List<Player> getAllPlayer(@PathVariable String code){
        return new ArrayList<>(playerStore.findAllByGame(gameStore.findById(code)));
    }

    @PostMapping("/create-game")
    public String createGame(@RequestBody Game game) throws IOException {
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        do{
            for (int i = 0;i<4;i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameStore.containsId(code.toString()));

        game = new Game();
        game.setCode(code.toString());
        game.setMap(rawMapService.convertJsonToMap(rawMapService.loadRandomMap()).getName());
        game.setTime(0);
        game.setRunning(true);
        gameStore.save(game);

        RawMapData rawMapData = rawMapService.convertJsonToMap(rawMapService.loadRandomMap());

        List<Heal> heals = new ArrayList<>();
        for (int i = 0; i < rawMapData.getHeal_pad_spawn().size(); i++){
            Heal heal = new Heal(
                    rawMapData.getHeal_pad_spawn().get(i).getX(),
                    rawMapData.getHeal_pad_spawn().get(i).getY(),
                    true,
                    0,
                    code.toString());
            Heal heal1 = healStore.save(heal);
            System.out.println(heal1);
            heals.add(heal1);
        }
        List<Obstacles> obstacles = new ArrayList<>();
        for (int i = 0; i < rawMapData.getObstacles().size(); i++){
            Obstacles obstacle = new Obstacles(code.toString(),rawMapData.getObstacles().get(i),100);
            obstacles.add(obstacleStore.save(obstacle));
        }

        MapData mapData = new MapData(rawMapData);
        mapData.setCode(code.toString());
        mapData.setObstacles(obstacles);
        mapData.setHeal_pads(heals);
        mapData.setPower_ups(new ArrayList<>());
        mapDataStore.save(mapData);


        mapDataStore.save(mapData);

        return code.toString();
    }

    @GetMapping("/get-map-data/{code}")
    public MapData getTest(@PathVariable String code) {
        return mapDataStore.findById(code);
    }

    @GetMapping("/get-game/{code}")
    public Game getGame(@PathVariable String code){
        return gameStore.findById(code);
    }

}

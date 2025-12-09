package de.ryoshi.minigame.controller;

import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.model.MapData;
import de.ryoshi.minigame.service.RawMapService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.model.RawMapData;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
public class GameRestController {

    private final PlayerStore playerStore;
    private final GameStore gameStore;
    private final RawMapService rawMapService;

    @GetMapping("/get-all-player/{code}")
    public List<Player> getAllPlayer(@PathVariable String code){
        return new ArrayList<>(playerStore.findAllByGame(gameStore.findById(code)));
    }

    @PostMapping("/create-game")
    public String createGame() throws IOException {
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        do{
            for (int i = 0; i < 4; i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameStore.containsId(code.toString()));

        RawMapData rawMapData = rawMapService.convertJsonToMap(rawMapService.loadRandomMap());

        Game game = new Game(rawMapData);
        game.setCode(code.toString());
        game.setTime(0);
        game.setRunning(true);
        gameStore.save(game);

        return game.getCode();
    }

    @GetMapping("/get-map-data/{code}")
    public MapData getTest(@PathVariable String code) {
        return gameStore.findById(code).getMapData();
    }

    @GetMapping("/get-game/{code}")
    public Game getGame(@PathVariable String code){
        return gameStore.findById(code);
    }

}

package com.dt.minigame.controller;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Player;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class GameRestController {

    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;

    public GameRestController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @GetMapping("/get-all-player/{code}")
    public List<Player> getAllPlayer(@PathVariable String code){
        return new ArrayList<>(playerRepository.findAllByGame(gameRepository.findById(code).orElseThrow()));
    }

    @PostMapping("/create-game")
    public String createGame(@RequestBody Game game){
        System.out.println(game.getCode());
        String[] alphabet = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        StringBuilder code = new StringBuilder();
        do{
            for (int i = 0;i<4;i++){
                code.append(alphabet[(int) (Math.random()*(alphabet.length))]);
            }
        }while (gameRepository.findById(code.toString()).isPresent());

        game = new Game();
        game.setCode(code.toString());
        gameRepository.save(game);
        return code.toString();
    }

}

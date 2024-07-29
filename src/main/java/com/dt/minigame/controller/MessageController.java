package com.dt.minigame.controller;

import com.dt.minigame.model.Bullet;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.model.Player;
import com.dt.minigame.repository.BulletRepository;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Objects;

@Controller
@CrossOrigin
public class MessageController {

    private final PlayerRepository playerRepository;
    private final BulletRepository bulletRepository;
    private final GameRepository gameRepository;

    public MessageController(PlayerRepository playerRepository, BulletRepository bulletRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.bulletRepository = bulletRepository;
        this.gameRepository = gameRepository;
    }

    @MessageMapping("/game.join/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onJoin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        System.out.println(message.getPlayer() + " joined the game");
        headerAccessor.getSessionAttributes().put("username", message.getPlayer());
        headerAccessor.getSessionAttributes().put("code", message.getCode());
        Player player = new Player();
        player.setUsername(message.getPlayer());
        player.setGame(gameRepository.findById(message.getCode()).orElseThrow());
        player.setKillCounter(0);
        player.setDeathCounter(0);
        player.setHp(100);
        playerRepository.save(player);
        //TODO: Handle Frontend
        return message;
    }

    @MessageMapping("/game.spawn/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onSpawn(@Payload Message message){
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setX(0);
        player.setY(0);
        return message;
    }

    @MessageMapping("/game.position/{code}")
    @SendTo("/start-game/game/{code}")
    public Message pos(@Payload Message message){
        String[] pos = message.getContent().split(",");
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setX(Double.parseDouble(pos[0]));
        player.setX(Double.parseDouble(pos[1]));
        playerRepository.save(player);
        return message;
    }

    @MessageMapping("/game.shoot/{code}")
    @SendTo("/start-game/game/{code}")
    public Message shoot(@Payload Message message){
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        String[] content = message.getContent().split(",");
        Bullet bullet = new Bullet();
        bullet.setX(Double.parseDouble(content[0]));
        bullet.setY(Double.parseDouble(content[1]));
        bullet.setAngle(Double.parseDouble(content[2]));
        bullet.setSpeed(Double.parseDouble(content[3]));
        Bullet shotBullet = bulletRepository.save(bullet);
        message.setContent(shotBullet.toString());
        return message;
    }

    @MessageMapping("/game.delete-bullet/{code}")
    @SendTo("/start-game/game/{code}")
    public Message deleteBullet(@Payload Message message){
        if (bulletRepository.existsById(Integer.parseInt(message.getContent()))) bulletRepository.deleteById(Integer.parseInt(message.getContent()));
        return message;
    }

    @MessageMapping("/game.player-hit/{code}")
    @SendTo("/start-game/game/{code}")
    public Message playerHit(@Payload Message message){
        String[] args = message.getContent().split(",");
        Player killer = playerRepository.findById(message.getPlayer()).orElseThrow();
        Player shotPlayer = playerRepository.findById(args[0]).orElseThrow();

        if (shotPlayer.getHp() - Integer.parseInt(args[1]) <= 0){
            killer.setKillCounter(killer.getKillCounter() + 1);
            shotPlayer.setDeathCounter(shotPlayer.getDeathCounter() + 1);
            shotPlayer.setX(0);
            shotPlayer.setY(0);
            shotPlayer.setHp(100);
            playerRepository.save(killer);
            playerRepository.save(shotPlayer);
            message.setContent(shotPlayer.getUsername());
            message.setType(MessageType.KILLED);
        }else {
            shotPlayer.setHp(shotPlayer.getHp() - Integer.parseInt(args[1]));
            playerRepository.save(shotPlayer);
        }
        return message;
    }

}

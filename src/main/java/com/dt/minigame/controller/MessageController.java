package com.dt.minigame.controller;

import com.dt.minigame.model.Bullet;
import com.dt.minigame.model.Message;
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
        playerRepository.save(new Player(message.getPlayer(),0,0,gameRepository.findById(message.getCode()).orElseThrow()));
        return message;
    }

    @MessageMapping("/game.position/{code}")
    @SendTo("/start-game/game/{code}")
    public Message pos(@Payload Message message){
        String[] pos = message.getContent().split(",");
        Player player = new Player(message.getPlayer(),Integer.parseInt(pos[0]),Integer.parseInt(pos[1]),gameRepository.findById(message.getCode()).orElseThrow());
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
        Bullet shotBullet = bulletRepository.save(bullet);
        message.setContent(shotBullet.toString());
        return message;
    }

    @MessageMapping("/game.delete-bullet/{code}")
    @SendTo("/start-game/game/{code}")
    public Message deleteBullet(@Payload Message message){
        bulletRepository.deleteById(Integer.parseInt(message.getContent()));
        return message;
    }

    @MessageMapping("/game.player-hit/{code}")
    @SendTo("/start-game/game/{code}")
    public Message playerHit(@Payload Message message){
        //TODO: Kill Counter in data bank
        return message;
    }

}

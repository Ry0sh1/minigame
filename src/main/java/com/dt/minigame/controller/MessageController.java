package com.dt.minigame.controller;

import com.dt.minigame.model.Bullet;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.Player;
import com.dt.minigame.repository.BulletRepository;
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

    public MessageController(PlayerRepository playerRepository, BulletRepository bulletRepository) {
        this.playerRepository = playerRepository;
        this.bulletRepository = bulletRepository;
    }

    @MessageMapping("/game.wrongAnswer/")
    @SendTo("/start-game/game/")
    public Message wrongAnswer(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.join/")
    @SendTo("/start-game/game/")
    public Message onJoin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        System.out.println(message.getPlayer() + " joined the game");
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username",message.getPlayer());
        playerRepository.save(new Player(message.getPlayer(),0,0));
        return message;
    }

    @MessageMapping("/game.pos/")
    @SendTo("/start-game/game/")
    public Message pos(@Payload Message message){
        String[] pos = message.getContent().split(",");
        Player player = new Player(message.getPlayer(),Integer.parseInt(pos[0]),Integer.parseInt(pos[1]));
        playerRepository.save(player);
        return message;
    }

    @MessageMapping("/game.shoot/")
    @SendTo("/start-game/game/")
    public Message shoot(@Payload Message message){
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        Bullet bullet = new Bullet();
        //Player Width & Height / 2
        bullet.setX(player.getX() + 5);
        bullet.setY(player.getY() + 5);
        bullet.setAngle(Double.parseDouble(message.getContent()));
        Bullet shotBullet = bulletRepository.save(bullet);
        message.setContent(shotBullet.toString());
        return message;
    }

}

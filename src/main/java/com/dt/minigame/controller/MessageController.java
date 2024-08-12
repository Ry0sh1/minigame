package com.dt.minigame.controller;

import com.dt.minigame.model.*;
import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.scheduled.GameTimer;
import com.dt.minigame.util.Constant;
import com.dt.minigame.repository.BulletRepository;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.HealRepository;
import com.dt.minigame.repository.PlayerRepository;
import com.dt.minigame.service.RawMapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

@Controller
@CrossOrigin
public class MessageController {

    private final PlayerRepository playerRepository;
    private final BulletRepository bulletRepository;
    private final GameRepository gameRepository;
    private final HealRepository healRepository;
    private final RawMapService rawMapService;
    private final ObjectMapper objectMapper;

    public MessageController(PlayerRepository playerRepository, BulletRepository bulletRepository, GameRepository gameRepository, HealRepository healRepository, RawMapService rawMapService, ObjectMapper objectMapper) {
        this.playerRepository = playerRepository;
        this.bulletRepository = bulletRepository;
        this.gameRepository = gameRepository;
        this.healRepository = healRepository;
        this.rawMapService = rawMapService;
        this.objectMapper = objectMapper;
    }

    @MessageMapping("/game.join/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onJoin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        headerAccessor.getSessionAttributes().put("username", message.getPlayer());
        headerAccessor.getSessionAttributes().put("code", message.getCode());
        Player player = new Player();
        player.setUsername(message.getPlayer());
        player.setGame(gameRepository.findById(message.getCode()).orElseThrow());
        player.setKillCounter(0);
        player.setDeathCounter(0);
        player.setHp(Constant.MAX_HP);
        player.setAlive(true);
        playerRepository.save(player);
        message.setContent(objectMapper.writeValueAsString(gameRepository.findById(message.getCode()).orElseThrow()));
        //TODO: Handle Frontend
        return message;
    }

    @MessageMapping("/game.spawn/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onSpawn(@Payload Message message) throws IOException {
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setWeapon(message.getContent());
        GameTimer.playerSetRandomSpawnPoint(player, rawMapService);
        player.setAlive(true);
        playerRepository.save(player);
        message.setContent(player.toString());
        return message;
    }

    @MessageMapping("/game.position/{code}")
    @SendTo("/start-game/game/{code}")
    public Message pos(@Payload Message message){
        String[] pos = message.getContent().split(",");
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setX(Double.parseDouble(pos[0]));
        player.setY(Double.parseDouble(pos[1]));
        playerRepository.save(player);
        return message;
    }

    @MessageMapping("/game.shoot/{code}")
    @SendTo("/start-game/game/{code}")
    public Message shoot(@Payload Message message){
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
            shotPlayer.setAlive(false);
            shotPlayer.setX(0);
            shotPlayer.setY(0);
            shotPlayer.setHp(Constant.MAX_HP);
            shotPlayer.setRespawnTimer(Constant.RESPAWN_TIMER);
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

    @MessageMapping("/game.heal/{code}")
    @SendTo("/start-game/game/{code}")
    public Message heal(@Payload Message message){
        Heal heal = healRepository.findById(Integer.parseInt(message.getContent())).orElseThrow();
        heal.setActive(false);
        heal.setCooldown(Constant.HEAL_COOLDOWN);
        healRepository.save(heal);
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setHp(Math.min(player.getHp() + Constant.HEAL, Constant.MAX_HP));
        playerRepository.save(player);
        return message;
    }

    @MessageMapping("/game.view-angle/{code}")
    @SendTo("/start-game/game/{code}")
    public Message viewAngle(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.change-weapon/{code}")
    public void changeWeapon(@Payload Message message){
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setWeapon(message.getContent());
        playerRepository.save(player);
    }

}

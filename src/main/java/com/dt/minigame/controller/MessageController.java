package com.dt.minigame.controller;

import com.dt.minigame.model.*;
import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.model.MapData.MapData;
import com.dt.minigame.model.MapData.PowerUp;
import com.dt.minigame.repository.*;
import com.dt.minigame.scheduled.GameTimer;
import com.dt.minigame.service.AsyncService;
import com.dt.minigame.service.PowerUpService;
import com.dt.minigame.util.Constant;
import com.dt.minigame.service.RawMapService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin
public class MessageController {

    private final PlayerRepository playerRepository;
    private final BulletRepository bulletRepository;
    private final GameRepository gameRepository;
    private final HealRepository healRepository;
    private final RawMapService rawMapService;
    private final ObjectMapper objectMapper;
    private final AsyncService asyncService;
    private final SimpMessageSendingOperations messagingTemplate;
    private final PowerUpService powerUpService;

    public MessageController(PlayerRepository playerRepository,
                             BulletRepository bulletRepository,
                             GameRepository gameRepository,
                             HealRepository healRepository,
                             RawMapService rawMapService,
                             ObjectMapper objectMapper,
                             AsyncService asyncService,
                             SimpMessageSendingOperations messagingTemplate,
                             PowerUpService powerUpService) {
        this.playerRepository = playerRepository;
        this.bulletRepository = bulletRepository;
        this.gameRepository = gameRepository;
        this.healRepository = healRepository;
        this.rawMapService = rawMapService;
        this.objectMapper = objectMapper;
        this.asyncService = asyncService;
        this.messagingTemplate = messagingTemplate;
        this.powerUpService = powerUpService;
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

    @MessageMapping("/game.shotgun-shot/{code}")
    public void shotgunShot(@Payload Message message){
        Player killer = playerRepository.findById(message.getPlayer()).orElseThrow();
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (String username : message.getContent().split(",")){
            if (username != null){
                nameCountMap.put(username, nameCountMap.getOrDefault(username, 0) + 1);
            }
        }

        for (Map.Entry<String, Integer> entry : nameCountMap.entrySet()) {
            Player shotPlayer = playerRepository.findById(entry.getKey()).orElseThrow();
            Message hitMessage = new Message();
            int damage = entry.getValue() * Constant.SHOTGUN_DAMAGE;
            hitMessage.setContent(shotPlayer.getUsername() + "," + damage);
            hitMessage.setCode(message.getCode());
            hitMessage.setPlayer(message.getPlayer());
            if (shotPlayer.getHp() - damage <= 0){
                hitMessage.setType(MessageType.KILLED);
                hitMessage.setContent(shotPlayer.getUsername());
            }else {
                hitMessage.setType(MessageType.PLAYER_HIT);
            }
            asyncService.processPlayerHit(killer, shotPlayer, damage);
            messagingTemplate.convertAndSend("/start-game/game/"+hitMessage.getCode(),hitMessage);
        }
    }
    @MessageMapping("/game.player-hit/{code}")
    @SendTo("/start-game/game/{code}")
    public Message playerHit(@Payload Message message) {
        String[] args = message.getContent().split(",");
        Player killer = playerRepository.findById(message.getPlayer()).orElseThrow();
        Player shotPlayer = playerRepository.findById(args[0]).orElseThrow();
        int damage = Integer.parseInt(args[1]);
        int allHP = shotPlayer.getHp() + shotPlayer.getShield();
        if (allHP - damage <= 0){
            message.setType(MessageType.KILLED);
            message.setContent(shotPlayer.getUsername());
        }
        asyncService.processPlayerHit(killer, shotPlayer, damage);
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

    @MessageMapping("/game.take-powerup/{code}")
    @SendTo("/start-game/game/{code}")
    public Message takePowerUp(@Payload Message message){
        powerUpService.deletePowerUpById(Integer.parseInt(message.getContent()));
        return message;
    }

    @MessageMapping("/game.use-powerup/{code}")
    @SendTo("/start-game/game/{code}")
    public Message usePowerUp(@Payload Message message){
        if (message.getContent().equals("shield")){
            Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
            if (player.getShield() + Constant.SHIELD_AMOUNT <= 100){
                player.setShield(player.getShield() + Constant.SHIELD_AMOUNT);
                playerRepository.save(player);
            }
        }
        return message;
    }
    @MessageMapping("/game.change-weapon/{code}")
    public void changeWeapon(@Payload Message message){
        Player player = playerRepository.findById(message.getPlayer()).orElseThrow();
        player.setWeapon(message.getContent());
        playerRepository.save(player);
    }

}

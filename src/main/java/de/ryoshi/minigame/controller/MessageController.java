package de.ryoshi.minigame.controller;

import de.ryoshi.minigame.model.Bullet;
import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.MessageType;
import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.model.Position;
import de.ryoshi.minigame.scheduled.GameTimer;
import de.ryoshi.minigame.service.AsyncService;
import de.ryoshi.minigame.service.RawMapService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class MessageController {

    private final PlayerStore playerStore;
    private final GameStore gameStore;
    private final ObjectMapper objectMapper;
    private final AsyncService asyncService;
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/game.join/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onJoin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        headerAccessor.getSessionAttributes().put("username", message.getPlayer());
        headerAccessor.getSessionAttributes().put("code", message.getCode());
        Player player = new Player();
        player.setUsername(message.getPlayer());
        player.setGame(gameStore.findById(message.getCode()));
        player.setKillCounter(0);
        player.setDeathCounter(0);
        player.setHp(Constant.MAX_HP);
        player.setAlive(true);
        playerStore.save(player);
        message.setContent(objectMapper.writeValueAsString(gameStore.findById(message.getCode())));
        //TODO: Handle Frontend
        return message;
    }

    @MessageMapping("/game.spawn/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onSpawn(@Payload Message message) {
        Player player = playerStore.findById(message.getPlayer());
        player.setWeapon(message.getContent());
        GameTimer.playerSetRandomSpawnPoint(player);
        player.setAlive(true);
        playerStore.save(player);
        message.setContent(player.toString());
        return message;
    }

    @MessageMapping("/game.position/{code}")
    @SendTo("/start-game/game/{code}")
    public Message pos(@Payload Message message){
        String[] pos = message.getContent().split(",");
        Player player = playerStore.findById(message.getPlayer());
        player.setX(Double.parseDouble(pos[0]));
        player.setY(Double.parseDouble(pos[1]));
        playerStore.save(player);
        return message;
    }

    @MessageMapping("/game.shoot/{code}")
    @SendTo("/start-game/game/{code}")
    public Message shoot(@Payload Message message){
        Game game = gameStore.findById(message.getCode());
        String[] content = message.getContent().split(",");
        Bullet bullet = new Bullet();
        bullet.setX(Double.parseDouble(content[0]));
        bullet.setY(Double.parseDouble(content[1]));
        bullet.setAngle(Double.parseDouble(content[2]));
        bullet.setSpeed(Double.parseDouble(content[3]));

        message.setContent(game.shootBullet(bullet).toString());
        return message;
    }

    @MessageMapping("/game.delete-bullet/{code}")
    @SendTo("/start-game/game/{code}")
    public Message deleteBullet(@Payload Message message){
        Game game = gameStore.findById(message.getCode());
        game.deleteBullet(Integer.parseInt(message.getContent()));
        return message;
    }

    @MessageMapping("/game.shotgun-shot/{code}")
    public void shotgunShot(@Payload Message message){
        Player killer = playerStore.findById(message.getPlayer());
        Map<String, Integer> nameCountMap = new HashMap<>();
        for (String username : message.getContent().split(",")){
            nameCountMap.put(username, nameCountMap.getOrDefault(username, 0) + 1);
        }

        for (Map.Entry<String, Integer> entry : nameCountMap.entrySet()) {
            Player shotPlayer = playerStore.findById(entry.getKey());
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
        Player killer = playerStore.findById(message.getPlayer());
        Player shotPlayer = playerStore.findById(args[0]);
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
        Game game = gameStore.findById(message.getCode());
        game.healUsed(Integer.parseInt(message.getContent()));
        Player player = playerStore.findById(message.getPlayer());
        player.setHp(Math.min(player.getHp() + Constant.HEAL, Constant.MAX_HP));
        playerStore.save(player);
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
        Game game = gameStore.findById(message.getCode());
        game.powerUpPicked(Integer.parseInt(message.getContent()));
        return message;
    }

    @MessageMapping("/game.use-powerup/{code}")
    @SendTo("/start-game/game/{code}")
    public Message usePowerUp(@Payload Message message){
        if (message.getContent().equals("shield")){
            Player player = playerStore.findById(message.getPlayer());
            if (player.getShield() + Constant.SHIELD_AMOUNT <= 100){
                player.setShield(player.getShield() + Constant.SHIELD_AMOUNT);
                playerStore.save(player);
            }
        }
        return message;
    }
    @MessageMapping("/game.change-weapon/{code}")
    public void changeWeapon(@Payload Message message){
        Player player = playerStore.findById(message.getPlayer());
        player.setWeapon(message.getContent());
        playerStore.save(player);
    }

}

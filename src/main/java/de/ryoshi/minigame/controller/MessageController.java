package de.ryoshi.minigame.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.model.Position;
import de.ryoshi.minigame.scheduled.GameTimer;
import de.ryoshi.minigame.service.WeaponService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class MessageController {

    private final PlayerStore playerStore;
    private final GameStore gameStore;
    private final ObjectMapper objectMapper;
    private final WeaponService weaponService;

    @MessageMapping("/game.join/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onJoin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) throws JsonProcessingException {
        headerAccessor.getSessionAttributes().put("username", message.getPlayer());
        headerAccessor.getSessionAttributes().put("code", message.getCode());
        Player player = new Player(message.getPlayer(), message.getCode());
        playerStore.save(player);
        message.setContent(objectMapper.writeValueAsString(gameStore.findById(message.getCode())));
        //TODO: Handle Frontend
        return message;
    }

    @MessageMapping("/game.move/{code}")
    public void move(@Payload Message message) {
        switch (message.getContent()) {
            case "w" -> playerStore.findById(message.getPlayer()).getInput().setUp(true);
            case "a" -> playerStore.findById(message.getPlayer()).getInput().setLeft(true);
            case "s" -> playerStore.findById(message.getPlayer()).getInput().setDown(true);
            case "d" -> playerStore.findById(message.getPlayer()).getInput().setRight(true);
        }
    }

    @MessageMapping("/game.stop-move/{code}")
    public void stopMove(@Payload Message message) {
        switch (message.getContent()) {
            case "w" -> playerStore.findById(message.getPlayer()).getInput().setUp(false);
            case "a" -> playerStore.findById(message.getPlayer()).getInput().setLeft(false);
            case "s" -> playerStore.findById(message.getPlayer()).getInput().setDown(false);
            case "d" -> playerStore.findById(message.getPlayer()).getInput().setRight(false);
        }
    }

    @MessageMapping("/game.spawn/{code}")
    @SendTo("/start-game/game/{code}")
    public Message onSpawn(@Payload Message message) throws IOException {
        Player player = playerStore.findById(message.getPlayer());
        player.setWeapon(weaponService.loadWeaponByName(message.getContent()));
        Position spawn = gameStore.findById(message.getCode()).getRandomSpawnPoint();
        player.setX(spawn.getX());
        player.setY(spawn.getY());
        player.setAlive(true);
        playerStore.save(player);
        message.setContent(player.toString());
        return message;
    }

    @MessageMapping("/game.shoot/{code}")
    public void shoot(@Payload Message message){
        playerStore.findById(message.getPlayer()).getInput().setShoot(Boolean.parseBoolean(message.getContent()));
    }

    @MessageMapping("/game.view-angle/{code}")
    public void viewAngle(@Payload Message message){
        playerStore.findById(message.getPlayer()).setAngle(Double.parseDouble(message.getContent()));
    }

    @MessageMapping("/game.use-powerup/{code}")
    @SendTo("/start-game/game/{code}")
    public Message usePowerUp(@Payload Message message){
        if (message.getContent().equals("shield")){
            Player player = playerStore.findById(message.getPlayer());
            if (player.getShield() + Constant.SHIELD_AMOUNT <= 100){
                player.setShield(player.getShield() + Constant.SHIELD_AMOUNT);
            }
        }
        return message;
    }

    @MessageMapping("/game.change-weapon/{code}")
    public void changeWeapon(@Payload Message message) throws IOException {
        Player player = playerStore.findById(message.getPlayer());
        player.setWeapon(weaponService.loadWeaponByName(message.getContent()));
    }

}

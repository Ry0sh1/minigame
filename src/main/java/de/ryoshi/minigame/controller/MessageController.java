package de.ryoshi.minigame.controller;

import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.service.GameService;
import de.ryoshi.minigame.service.WeaponService;
import de.ryoshi.minigame.stores.GameStore;
import de.ryoshi.minigame.stores.PlayerStore;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.util.Map;

@Controller
@CrossOrigin
@RequiredArgsConstructor
public class MessageController {

    private final PlayerStore playerStore;
    private final WeaponService weaponService;
    private final GameService gameService;
    private final GameStore gameStore;

    @MessageMapping("/game.move")
    public void move(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        Player player = getPlayer(headerAccessor);
        switch (message.getContent()) {
            case "w" -> player.getInput().setUp(true);
            case "a" -> player.getInput().setLeft(true);
            case "s" -> player.getInput().setDown(true);
            case "d" -> player.getInput().setRight(true);
        }
    }

    @MessageMapping("/game.stop-move")
    public void stopMove(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        Player player = getPlayer(headerAccessor);
        switch (message.getContent()) {
            case "w" -> player.getInput().setUp(false);
            case "a" -> player.getInput().setLeft(false);
            case "s" -> player.getInput().setDown(false);
            case "d" -> player.getInput().setRight(false);
        }
    }

    @MessageMapping("/game.shoot")
    public void shoot(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        getPlayer(headerAccessor).getInput().setShoot(Boolean.parseBoolean(message.getContent()));
    }

    @MessageMapping("/game.view-angle")
    public void viewAngle(@Payload Message message, SimpMessageHeaderAccessor headerAccessor){
        getPlayer(headerAccessor).setAngle(Double.parseDouble(message.getContent()));
    }

    @MessageMapping("/game.use-powerup")
    public void usePowerUp(SimpMessageHeaderAccessor headerAccessor){
        gameService.usePowerUp(getGame(headerAccessor), getPlayer(headerAccessor));
    }

    @MessageMapping("/game.change-weapon")
    public void changeWeapon(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) throws IOException {
        getPlayer(headerAccessor).setWeapon(weaponService.loadWeaponByName(message.getContent()));
    }

    public Player getPlayer(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> session = headerAccessor.getSessionAttributes();
        if (session == null || session.get("username") == null) {
            throw new IllegalStateException("No username in WebSocket session");
        }
        return playerStore.findById((String) session.get("username"));
    }

    public Game getGame(SimpMessageHeaderAccessor headerAccessor) {
        Map<String, Object> session = headerAccessor.getSessionAttributes();
        if (session == null || session.get("code") == null) {
            throw new IllegalStateException("No Code in WebSocket session");
        }
        return gameStore.findById((String) session.get("code"));
    }
}

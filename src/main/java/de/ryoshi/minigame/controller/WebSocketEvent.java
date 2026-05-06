package de.ryoshi.minigame.controller;

import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.MessageType;
import de.ryoshi.minigame.service.GameService;
import de.ryoshi.minigame.stores.PlayerStore;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class WebSocketEvent {

    private final PlayerStore playerStore;
    private final GameService gameService;
    private final SimpMessageSendingOperations messagingTemplate;

    public WebSocketEvent(PlayerStore playerStore, GameService gameService, SimpMessageSendingOperations messagingTemplate) {
        this.playerStore = playerStore;
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String code = headerAccessor.getSessionAttributes().get("code").toString();
        playerStore.deleteById(username);
        if (playerStore.findAllByGame(gameService.findByCode(code)).isEmpty()){
            gameService.deleteGame(code);
        }
        Message message = new Message("Left the game", MessageType.LEFT);
        messagingTemplate.convertAndSend("/start-game/game/" + code, message);
    }

}

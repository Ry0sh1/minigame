package com.dt.minigame.controller;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.repository.GameRepository;
import com.dt.minigame.repository.PlayerRepository;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class Event {

    private final PlayerRepository playerRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    public Event(PlayerRepository playerRepository, SimpMessageSendingOperations messagingTemplate) {
        this.playerRepository = playerRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) Objects.requireNonNull(headerAccessor.getSessionAttributes()).get("username");
        String code = headerAccessor.getSessionAttributes().get("code").toString();
        System.out.println(username + " left the game");
        playerRepository.delete(playerRepository.findById(username).orElseThrow());
        Message message = new Message(username, "Left the game", MessageType.LEFT, code);
        messagingTemplate.convertAndSend("/start-game/game/" + code, message);
    }

}

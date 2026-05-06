package de.ryoshi.minigame.configuration;

import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.stores.PlayerStore;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PlayerStore playerStore;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = accessor.getFirstNativeHeader("username");
        String code = accessor.getFirstNativeHeader("code");

        accessor.getSessionAttributes().put("username", username);
        accessor.getSessionAttributes().put("code", code);


        Player player = new Player(username, code);
        playerStore.save(player);
    }

}

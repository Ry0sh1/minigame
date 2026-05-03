package de.ryoshi.minigame.service;

import de.ryoshi.minigame.model.Game;
import de.ryoshi.minigame.model.Message;
import de.ryoshi.minigame.model.MessageType;
import de.ryoshi.minigame.util.Constant;
import de.ryoshi.minigame.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {

    private final FileUtil fileUtil;
    private final SimpMessageSendingOperations messagingTemplate;

    public void sendEventMessage(Game game) {
        try {
            game.setCurrentEvent(fileUtil.convertJsonToJustName(fileUtil.getRandomJSONFromDirectory("classpath:assets/events")).getName());
        } catch (IOException e) {
            //Nothing
        }
        game.setCurrentEventTime(Constant.EVENT_TIME);
        game.setEvent(true);
        Message message = new Message();
        message.setType(MessageType.EVENT);
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setContent(game.getCurrentEvent());
        messagingTemplate.convertAndSend("/start-game/game/" + game.getCode(),message);
    }

    public void handleEvent(Game game) {
        if (game.isEvent()) {
            game.setCurrentEventTime(game.getCurrentEventTime() - 1);
            if (game.getCurrentEventTime() <= 0){
                stopEvent(game);
            }
            switch (game.getCurrentEvent()) {
                case "Tower": {
                    Random random = new Random();
                    double x = random.nextDouble(game.getMapData().getWidth());
                    double y = random.nextDouble(game.getMapData().getHeight());

                    Message message = new Message();
                    message.setType(MessageType.TOWER);
                    message.setPlayer("server");
                    message.setContent(x + "," + y + "," + game.getTime());
                    message.setCode(game.getCode());
                    messagingTemplate.convertAndSend("/start-game/game/" + game.getCode(),message);
                }
            }
        }
    }

    public void stopEvent(Game game){
        Message message = new Message();
        message.setType(MessageType.STOP_EVENT);
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setContent(game.getCurrentEvent());
        game.setCurrentEvent(null);
        game.setEvent(false);
        game.setCurrentEventTime(0);
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

}

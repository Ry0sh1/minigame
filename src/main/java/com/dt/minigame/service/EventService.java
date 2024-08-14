package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.util.Constant;
import com.dt.minigame.util.FileUtil;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EventService {

    private final FileUtil fileUtil;
    private final SimpMessageSendingOperations messagingTemplate;

    public EventService(FileUtil fileUtil, SimpMessageSendingOperations messagingTemplate) {
        this.fileUtil = fileUtil;
        this.messagingTemplate = messagingTemplate;
    }

    public void sendEventMessage(Game game) throws IOException {
        game.setCurrentEvent(fileUtil.convertJsonToJustName(fileUtil.getRandomJSONFromDirectory("classpath:assets/events")).getName());
        game.setCurrentEventTime(Constant.EVENT_TIME);
        game.setEvent(true);
        Message message = new Message();
        message.setType(MessageType.EVENT);
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setContent(game.getCurrentEvent());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
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

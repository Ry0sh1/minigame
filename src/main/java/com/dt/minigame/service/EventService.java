package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.model.event.Event;
import com.dt.minigame.util.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EventService {

    private final ObjectMapper objectMapper;
    private final FileUtil fileUtil;
    private final SimpMessageSendingOperations messagingTemplate;

    public EventService(ObjectMapper objectMapper, FileUtil fileUtil, SimpMessageSendingOperations messagingTemplate) {
        this.objectMapper = objectMapper;
        this.fileUtil = fileUtil;
        this.messagingTemplate = messagingTemplate;
    }

    public String loadRandomEvent() throws IOException {
        return fileUtil.getRandomJSONFromDirectory("classpath:assets/events");
    }

    public String loadEventByName(String name) throws IOException {
        return fileUtil.loadJSONByNameInDirectory("classpath:assets/events", name);
    }

    public Event convertJsonToEvent(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, Event.class);
    }

    public void sendEventMessage(Game game) throws IOException {
        Message message = new Message();
        message.setType(MessageType.EVENT);
        message.setPlayer("server");
        message.setCode(game.getCode());
        message.setContent(convertJsonToEvent(loadRandomEvent()).getName());
        messagingTemplate.convertAndSend("/start-game/game/"+game.getCode(),message);
    }

}

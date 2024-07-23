package com.dt.minigame.controller;

import com.dt.minigame.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin
public class MessageController {

    @MessageMapping("/game.wrongAnswer/")
    @SendTo("/start-game/game/")
    public Message wrongAnswer(@Payload Message message){
        return message;
    }

    @MessageMapping("/game.join/")
    public void test(@Payload Message message){
        System.out.println(message.getContent());
    }

    @MessageMapping("/game.pos/")
    public void pos(@Payload Message message){
        System.out.println(message.getContent());
    }

}

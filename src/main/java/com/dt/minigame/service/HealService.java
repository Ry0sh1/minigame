package com.dt.minigame.service;

import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.stores.HealStore;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class HealService {

    private final HealStore healStore;
    private final SimpMessageSendingOperations messagingTemplate;

    public HealService(HealStore healStore, SimpMessageSendingOperations messagingTemplate) {
        this.healStore = healStore;
        this.messagingTemplate = messagingTemplate;
    }

    public void reactivateHeal(Heal heal){
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(heal.getCode());
        message.setType(MessageType.REACTIVATE_HEAL);
        message.setContent(String.valueOf(heal.getId()));
        messagingTemplate.convertAndSend("/start-game/game/"+heal.getCode(),message);
    }

    public ArrayList<Heal> findAll() {
        return new ArrayList<>(healStore.findAll());
    }

    public void save(Heal heal) {
        healStore.save(heal);
    }

}

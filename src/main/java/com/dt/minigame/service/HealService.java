package com.dt.minigame.service;

import com.dt.minigame.model.MapData.Heal;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.repository.HealRepository;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;


@Service
public class HealService {

    private final HealRepository healRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    public HealService(HealRepository healRepository, SimpMessageSendingOperations messagingTemplate) {
        this.healRepository = healRepository;
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
        ArrayList<Heal> heals = new ArrayList<>();
        healRepository.findAll().forEach(heals::add);
        return heals;
    }

    public void save(Heal heal) {
        healRepository.save(heal);
    }

}

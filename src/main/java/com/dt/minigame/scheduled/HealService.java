package com.dt.minigame.scheduled;

import com.dt.minigame.model.Heal;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.repository.HealRepository;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class HealService {

    private final SimpMessageSendingOperations messagingTemplate;
    private final HealRepository healRepository;

    public HealService(SimpMessageSendingOperations messagingTemplate, HealRepository healRepository) {
        this.messagingTemplate = messagingTemplate;
        this.healRepository = healRepository;
    }

    @Scheduled(fixedRate = 1000) //Jede Sekunde
    public void heal(){
        healRepository.findAll().forEach(heal -> {
            if (!heal.isActive()){
                heal.setCooldown(heal.getCooldown() - 1);
                if (heal.getCooldown() <= 0){
                    heal.setActive(false);
                    reactivateHeal(heal);
                }
                healRepository.save(heal);
            }
        });
    }

    public void reactivateHeal(Heal heal){
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(heal.getGame().getCode());
        message.setType(MessageType.REACTIVATE_HEAL);
        message.setContent(String.valueOf(heal.getId()));
        messagingTemplate.convertAndSend("/start-game/game/"+heal.getGame().getCode(),message);
    }

}

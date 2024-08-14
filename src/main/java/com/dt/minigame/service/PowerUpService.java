package com.dt.minigame.service;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.JSON.JustName;
import com.dt.minigame.model.MapData.MapData;
import com.dt.minigame.model.MapData.PowerUp;
import com.dt.minigame.model.Message;
import com.dt.minigame.model.MessageType;
import com.dt.minigame.repository.MapDataRepository;
import com.dt.minigame.repository.PowerUpRepository;
import com.dt.minigame.util.FileUtil;
import com.dt.minigame.util.map.Point;
import com.dt.minigame.util.map.RawMapData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class PowerUpService {

    private final RawMapService rawMapService;
    private final MapDataRepository mapDataRepository;
    private final PowerUpRepository powerUpRepository;
    private final FileUtil fileUtil;
    private final SimpMessageSendingOperations messagingTemplate;

    public PowerUpService(RawMapService rawMapService,
                          MapDataRepository mapDataRepository,
                          PowerUpRepository powerUpRepository,
                          FileUtil fileUtil,
                          SimpMessageSendingOperations messagingTemplate) {
        this.rawMapService = rawMapService;
        this.mapDataRepository = mapDataRepository;
        this.powerUpRepository = powerUpRepository;
        this.fileUtil = fileUtil;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void spawnPowerUp(Game game) throws IOException {
        RawMapData rawMapData = rawMapService.convertJsonToMap(rawMapService.loadMapByName(game.getMap()));
        MapData mapData = mapDataRepository.findById(game.getCode()).orElseThrow();
        List<PowerUp> powerUpList = mapData.getPower_ups();
        if (powerUpList != null && !powerUpList.isEmpty() && rawMapData.getPower_up_spawn().size() <= powerUpList.size()){
            //Erstelle kein neues Powerup
            return;
        }
        PowerUp powerUp = new PowerUp();
        Random random = new Random();
        int n = random.nextInt(rawMapData.getPower_up_spawn().size());
        Point powerUpSpawn = rawMapData.getPower_up_spawn().get(n);
        powerUp.setX(powerUpSpawn.getX());
        powerUp.setY(powerUpSpawn.getY());
        powerUp.setCode(game.getCode());
        powerUp.setName(fileUtil.convertJsonToJustName(fileUtil.getRandomJSONFromDirectory("classpath:assets/powerups")).getName());
        powerUpList.add(powerUp);
        mapData.setPower_ups(powerUpList);
        powerUpRepository.save(powerUp);
        mapDataRepository.save(mapData);
        sendPowerUpSpawnMessage(powerUp);
    }

    private void sendPowerUpSpawnMessage(PowerUp powerUp) {
        Message message = new Message();
        message.setPlayer("server");
        message.setCode(powerUp.getCode());
        message.setType(MessageType.SPAWN_POWERUP);
        message.setContent(powerUp.toString());
        messagingTemplate.convertAndSend("/start-game/game/"+powerUp.getCode(),message);
    }

}

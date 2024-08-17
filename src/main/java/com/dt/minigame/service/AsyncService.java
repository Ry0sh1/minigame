package com.dt.minigame.service;

import com.dt.minigame.model.Player;
import com.dt.minigame.repository.PlayerRepository;
import com.dt.minigame.util.Constant;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@EnableAsync
public class AsyncService {

    private final PlayerRepository playerRepository;

    public AsyncService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Async
    public void processPlayerHit(Player killer, Player shotPlayer, int damage) {
        if ((shotPlayer.getHp() + shotPlayer.getShield()) - damage <= 0) {
            killer.setKillCounter(killer.getKillCounter() + 1);
            shotPlayer.setDeathCounter(shotPlayer.getDeathCounter() + 1);
            shotPlayer.setAlive(false);
            shotPlayer.setX(0);
            shotPlayer.setY(0);
            shotPlayer.setHp(Constant.MAX_HP);
            shotPlayer.setRespawnTimer(Constant.RESPAWN_TIMER);
            playerRepository.saveAll(List.of(killer, shotPlayer));
        } else {
            int rest = shotPlayer.getShield() - damage;
            shotPlayer.setShield(shotPlayer.getShield() - damage);
            if (shotPlayer.getShield() < 0){
                shotPlayer.setHp(shotPlayer.getHp() - Math.abs(rest));
                shotPlayer.setShield(0);
            }
            playerRepository.save(shotPlayer);
        }
    }
}

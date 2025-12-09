package de.ryoshi.minigame.service;

import de.ryoshi.minigame.model.Player;
import de.ryoshi.minigame.model.Position;
import de.ryoshi.minigame.stores.PlayerStore;
import de.ryoshi.minigame.util.Constant;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@EnableAsync
public class AsyncService {

    private final PlayerStore playerStore;

    public AsyncService(PlayerStore playerStore) {
        this.playerStore = playerStore;
    }

    @Async
    public void processPlayerHit(Player killer, Player shotPlayer, int damage) {
        if ((shotPlayer.getHp() + shotPlayer.getShield()) - damage <= 0) {
            if (!killer.getUsername().equals(shotPlayer.getUsername())){
                killer.setKillCounter(killer.getKillCounter() + 1);
            }
            shotPlayer.setDeathCounter(shotPlayer.getDeathCounter() + 1);
            shotPlayer.setAlive(false);
            shotPlayer.setX(0);
            shotPlayer.setY(0);
            shotPlayer.setHp(Constant.MAX_HP);
            shotPlayer.setRespawnTimer(Constant.RESPAWN_TIMER);
            playerStore.saveAll(List.of(killer, shotPlayer));
        } else {
            int rest = shotPlayer.getShield() - damage;
            shotPlayer.setShield(shotPlayer.getShield() - damage);
            if (shotPlayer.getShield() < 0){
                shotPlayer.setHp(shotPlayer.getHp() - Math.abs(rest));
                shotPlayer.setShield(0);
            }
            playerStore.save(shotPlayer);
        }
    }
}

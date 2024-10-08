package com.dt.minigame.repository;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, String> {
    List<Player> findAllByGame(Game game);
}

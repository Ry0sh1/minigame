package com.dt.minigame.repository;

import com.dt.minigame.model.Game;
import com.dt.minigame.model.Heal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealRepository extends CrudRepository<Heal, Integer> {
    List<Heal> findAllByGame(Game game);
}

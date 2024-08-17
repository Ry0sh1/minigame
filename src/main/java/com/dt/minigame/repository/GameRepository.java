package com.dt.minigame.repository;

import com.dt.minigame.model.Game;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends CrudRepository<Game, String> {
    void deleteByCode(String code);
}

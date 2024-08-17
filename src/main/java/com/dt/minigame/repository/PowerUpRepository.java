package com.dt.minigame.repository;

import com.dt.minigame.model.MapData.PowerUp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PowerUpRepository extends CrudRepository<PowerUp, Integer> {
    void deleteAllByCode(String code);
}

package com.dt.minigame.repository;

import com.dt.minigame.model.MapData.Obstacles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObstacleRepository extends CrudRepository<Obstacles, Integer> {
    void deleteAllByCode(String code);
}

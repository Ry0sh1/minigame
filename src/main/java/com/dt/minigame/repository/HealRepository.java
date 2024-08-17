package com.dt.minigame.repository;

import com.dt.minigame.model.MapData.Heal;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealRepository extends CrudRepository<Heal, Integer> {
    void deleteAllByCode(String code);
}

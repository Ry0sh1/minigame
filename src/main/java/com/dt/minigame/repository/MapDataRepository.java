package com.dt.minigame.repository;

import com.dt.minigame.model.MapData.MapData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapDataRepository extends CrudRepository<MapData, String> {
}

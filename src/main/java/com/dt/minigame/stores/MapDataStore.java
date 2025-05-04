package com.dt.minigame.stores;

import com.dt.minigame.model.MapData.MapData;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class MapDataStore {

    private final HashMap<String, MapData> mapDataStore;

    public MapDataStore() {
        this.mapDataStore = new HashMap<>();
    }

    public MapData save(MapData mapData) {
        return mapDataStore.put(mapData.getCode(), mapData);
    }

    public MapData findById(String code) {
        return mapDataStore.get(code);
    }

    public void deleteById(String code) {
        mapDataStore.remove(code);
    }
}

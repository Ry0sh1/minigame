package com.dt.minigame.service;

import com.dt.minigame.util.FileUtil;
import com.dt.minigame.util.map.RawMapData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class RawMapService {

    private final ObjectMapper objectMapper;
    private final FileUtil fileUtil;

    public RawMapService(ObjectMapper objectMapper, FileUtil fileUtil) {
        this.objectMapper = objectMapper;
        this.fileUtil = fileUtil;
    }

    public String loadRandomMap() throws IOException {
        return fileUtil.getRandomJSONFromDirectory("classpath:assets/maps");
    }

    public String loadMapByName(String name) throws IOException {
        return fileUtil.loadJSONByNameInDirectory("classpath:assets/maps", name);
    }

    public RawMapData convertJsonToMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, RawMapData.class);
    }

}

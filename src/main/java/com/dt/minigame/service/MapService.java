package com.dt.minigame.service;

import com.dt.minigame.model.map.MapData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Service
public class MapService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;

    public MapService(ResourceLoader resourceLoader, ObjectMapper objectMapper) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
    }

    public String loadRandomMap() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:assets/maps");
        List<String> files = Files.walk(Paths.get(resource.getURI()))
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .toList();

        Random random = new Random();
        String randomFile = files.get(random.nextInt(files.size()));
        Resource mapResource = resourceLoader.getResource("classpath:assets/maps/" + randomFile);

        return new String(Files.readAllBytes(Paths.get(mapResource.getURI())));
    }

    public String loadMapByName(String name) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:assets/maps");
        Resource mapResource = resourceLoader.getResource("classpath:assets/maps/" + name + ".json");
        return new String(Files.readAllBytes(Paths.get(mapResource.getURI())));
    }

    public MapData convertJsonToMap(String json) throws JsonProcessingException {
        return objectMapper.readValue(json, MapData.class);
    }

}

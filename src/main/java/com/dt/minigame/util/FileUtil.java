package com.dt.minigame.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

@Component
public class FileUtil {

    private final ResourceLoader resourceLoader;

    public FileUtil(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String getRandomJSONFromDirectory(String directory) throws IOException {
        Resource resource = resourceLoader.getResource(directory);
        List<String> files = Files.walk(Paths.get(resource.getURI()))
                .filter(Files::isRegularFile)
                .map(path -> path.getFileName().toString())
                .toList();

        Random random = new Random();
        String randomFile = files.get(random.nextInt(files.size()));
        Resource mapResource = resourceLoader.getResource(directory + "/" + randomFile);

        return new String(Files.readAllBytes(Paths.get(mapResource.getURI())));
    }

    public String loadJSONByNameInDirectory(String directory, String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(directory);
        Resource mapResource = resourceLoader.getResource(directory + "/" + fileName + ".json");
        return new String(Files.readAllBytes(Paths.get(mapResource.getURI())));
    }

}

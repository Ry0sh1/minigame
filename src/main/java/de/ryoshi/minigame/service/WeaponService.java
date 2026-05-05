package de.ryoshi.minigame.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.ryoshi.minigame.model.Weapon;
import de.ryoshi.minigame.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeaponService {
    private final ObjectMapper objectMapper;
    private final FileUtil fileUtil;

    public Weapon loadWeaponByName(String name) throws IOException {
        String json = fileUtil.loadJSONByNameInDirectory("classpath:assets/weapons", name);
        return objectMapper.readValue(json, Weapon.class);
    }

    public List<Weapon> loadAllWeapons() {
        try {
            Resource resource = fileUtil.getResourceLoader().getResource("classpath:assets/weapons");

            return Files.walk(Paths.get(resource.getURI()))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        try {
                            String json = Files.readString(path);
                            return objectMapper.readValue(json, Weapon.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load weapons", e);
        }
    }
}

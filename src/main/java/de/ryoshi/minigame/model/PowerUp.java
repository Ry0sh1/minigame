package de.ryoshi.minigame.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PowerUp extends AbstractGameObject {

    private String name;
    private String code;

    @Override
    public String toString() {
        return "{\"id\":\"" + id + "\", " +
                "\"name\":\"" + name + "\", " +
                "\"x\":\"" + x + "\", " +
                "\"y\":\"" + y + "\"}";
    }
}

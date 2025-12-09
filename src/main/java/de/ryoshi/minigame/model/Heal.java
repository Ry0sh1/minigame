package de.ryoshi.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Heal extends AbstractGameObject {

    private double x;
    private double y;
    private boolean active;
    private int cooldown;

    public boolean tick() {
        if (!isActive()){
            setCooldown(getCooldown() - 1);
            if (getCooldown() <= 0){
                setActive(true);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Heal{" +
                "id=" + id +
                ", active=" + active +
                ", cooldown=" + cooldown +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}

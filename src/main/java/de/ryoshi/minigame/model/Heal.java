package de.ryoshi.minigame.model;

import de.ryoshi.minigame.util.Constant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Heal extends AbstractGameObject {
    private boolean active;
    private int cooldown;
    private int width;
    private int height;

    public Heal(double x, double y, boolean active, int cooldown) {
        super(x, y);
        this.active = active;
        this.cooldown = cooldown;
        this.width = Constant.HEAL_HITBOX_WIDTH;
        this.height = Constant.HEAL_HITBOX_HEIGHT;
    }

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

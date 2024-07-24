package com.dt.minigame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    private String username;
    private int x;
    private int y;

    @Override
    public String toString() {
        return "{\"username\":\"" + username + "\", \"x\":\"" + x + "\", \"y\":\"" + y + "\"}";
    }
}

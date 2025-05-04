package com.dt.minigame.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Game {

    private String code;
    private String map;
    private int time;
    private boolean running;
    private String currentEvent;
    private boolean isEvent;
    private int currentEventTime;

}

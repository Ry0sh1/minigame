package com.dt.minigame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GameController {

    @GetMapping("")
    public String getIndex(){
        return "index";
    }

    @GetMapping("/game/{code}")
    public String startGame(@PathVariable String code){
        return "game";
    }

}

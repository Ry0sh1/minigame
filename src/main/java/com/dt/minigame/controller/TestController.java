package com.dt.minigame.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("")
    public String getIndex(){
        return "index";
    }

}

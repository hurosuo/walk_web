package com.web.walk_web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @GetMapping("/")
    public String ok() {
        return "K-Legends-winner";
    }
}

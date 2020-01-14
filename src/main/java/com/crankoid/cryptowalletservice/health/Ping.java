package com.crankoid.cryptowalletservice.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class Ping {

    @GetMapping()
    public String healthCheck(){
        return "OK";
    }

}

package com.apnatime.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchConnectionsController {
    @GetMapping("/hello")
    public String getHello(){
        return "Hello world!";
    }
}

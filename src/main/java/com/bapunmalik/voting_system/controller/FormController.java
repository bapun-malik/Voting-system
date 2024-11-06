package com.bapunmalik.voting_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FormController {
    @GetMapping("/sign")
    public String signup(){
        return "signup";
    }
}

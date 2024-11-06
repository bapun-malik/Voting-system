package com.bapunmalik.voting_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
    @RequestMapping("/")
    public String showHomePage(){
        return "index";
    }
    @GetMapping("/feature")
    public String showFeaturePage(){
        return "feature";
    }

    @GetMapping("/about")
    public String showAboutPage(){
        return "about";
    }
    @GetMapping("/contact")
    public String showContactPage(){
        return "contact";
    }
}

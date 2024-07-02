package com.example.vinfan.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vinfan")
public class HomeController {

    //    ===================LOGIN======================
    @GetMapping("/login")
    public String login() {
        return "/login/index";
    }

    @GetMapping("/register")
    public String register() {
        return "/login/register";
    }

    @GetMapping("/forgot")
    public String forgot() {
        return "/login/forgot";
    }

    @GetMapping("/reset")
    public String reset() {
        return "/login/reset";
    }


}

package com.example.vinfan.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("/index")
    public String admin() {
        return "/admin/index";
    }
}

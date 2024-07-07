package com.example.vinfan.controller;


import com.example.vinfan.repository.PhieuGiamRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    PhieuGiamRepo pggRepo;

    @GetMapping("/index")
    public String admin() {
        return "/admin/index";
    }
}

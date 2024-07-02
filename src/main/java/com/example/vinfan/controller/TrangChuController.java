package com.example.vinfan.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/trang-chu/")
public class TrangChuController {

    @GetMapping("h")
    public String trangchu(){

        return "/admin/index";
    }
}

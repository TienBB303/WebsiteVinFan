package com.example.datn.controller.ThuocTinhController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class ThuocTinhController {
    @GetMapping("/thuoc-tinh")
    public String hienThiThuocTinh(){
        return "admin/thuoc_tinh/thuoc_tinh";
    }
}

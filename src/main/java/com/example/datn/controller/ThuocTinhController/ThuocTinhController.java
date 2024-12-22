package com.example.datn.controller.ThuocTinhController;

import com.example.datn.entity.thuoc_tinh.*;
import com.example.datn.repository.SPCTRepo;
import com.example.datn.repository.SanPhamRepo;
import com.example.datn.repository.ThuocTinhRepo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ThuocTinhController {
    @GetMapping("/thuoc-tinh")
    public String hienThiThuocTinh(){
        return "admin/thuoc_tinh/thuoc_tinh";
    }
}

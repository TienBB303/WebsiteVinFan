package com.example.datn.controller;
import com.example.datn.entity.CartItem;
import com.example.datn.entity.CartItemRequest;
import com.example.datn.entity.SanPhamChiTiet;
import com.example.datn.repository.SPCTRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/ban-hang")
public class BanHangController {

    @Autowired
    private SPCTRepo spctRepo;

    @GetMapping("/index")
    public String trangBan(Model model) {
        List<SanPhamChiTiet> danhSachSanPham = spctRepo.findAll();
        System.out.println("Danh sách sản phẩm: " + danhSachSanPham); // Debug line
        model.addAttribute("danhSachSanPham", danhSachSanPham);
        return "admin/ban_hang_tai_quay/index";
    }

    @GetMapping("/thanh-toan")
    public String thanhToan(){

        return "admin/ban_hang_tai_quay/index";
    }
}

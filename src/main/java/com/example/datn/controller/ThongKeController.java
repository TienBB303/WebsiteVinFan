package com.example.datn.controller;

import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/thong-ke")

public class ThongKeController {
    private final ThongKeService thongKeService;

    @GetMapping("/index")
    public String getThongKe(Model model) {
        List<ThongKeSanPhamResponse> list = this.thongKeService.getSanPhamBanChay();

        int currentDay = thongKeService.getCurrentDay();
        int currentWeek = thongKeService.getCurrentWeek();
        int currentMonth = thongKeService.getCurrentMonth();
        int currentYear = thongKeService.getCurrentYear();

        List<ThongKeResponse> thongKeListDay = thongKeService.getListDay();
        List<ThongKeResponse> thongKeListWeek = thongKeService.getListWeek();
        List<ThongKeResponse> thongKeListMonth = thongKeService.getListMonth();
        List<ThongKeResponse> thongKeListYear = thongKeService.getListYear();
        model.addAttribute("thongKeListDay", thongKeListDay);
        model.addAttribute("thongKeListWeek", thongKeListWeek);
        model.addAttribute("thongKeListMonth", thongKeListMonth);
        model.addAttribute("thongKeListYear", thongKeListYear);

        // Thêm biến current vào model
        model.addAttribute("currentDay", currentDay);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);
        //
        model.addAttribute("listSPBanChay",list);
        return "/admin/thong_ke/index";
    }
}

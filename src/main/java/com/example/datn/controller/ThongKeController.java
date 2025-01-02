package com.example.datn.controller;

import com.example.datn.dto.response.ThongKeResponse;
import com.example.datn.dto.response.ThongKeSanPhamResponse;
import com.example.datn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/thong-ke")

public class ThongKeController {
    private final ThongKeService thongKeService;

    @GetMapping("/index")
    public String getThongKe(Model model) {
//        List<ThongKeSanPhamResponse> list = this.thongKeService.getSanPhamBanChay();

        int currentDay = thongKeService.getCurrentDay();
        int currentWeek = thongKeService.getCurrentWeek();
        int currentMonth = thongKeService.getCurrentMonth();
        int currentYear = thongKeService.getCurrentYear();

//        int ngayHomQua = thongKeService.getYesterday();

        List<ThongKeResponse> thongKeListDay = thongKeService.getListDay();
        List<ThongKeResponse> thongKeListWeek = thongKeService.getListWeek();
        List<ThongKeResponse> thongKeListMonth = thongKeService.getListMonth();
        List<ThongKeResponse> thongKeListYear = thongKeService.getListYear();

//        List<ThongKeResponse> thongkeListngayHomQua = thongKeService.getListYesterDay();

        System.out.println("ngày :" + thongKeListDay);
        System.out.println("tuần :" +thongKeListWeek);
        System.out.println("tháng :" +thongKeListMonth);
        System.out.println("năm :" +thongKeListYear);

        model.addAttribute("thongKeListDay", thongKeListDay);
        model.addAttribute("thongKeListWeek", thongKeListWeek);
        model.addAttribute("thongKeListMonth", thongKeListMonth);
        model.addAttribute("thongKeListYear", thongKeListYear);

//        model.addAttribute("thongkeListngayHomQua", thongkeListngayHomQua);

        model.addAttribute("currentDay", currentDay);
        model.addAttribute("currentWeek", currentWeek);
        model.addAttribute("currentMonth", currentMonth);
        model.addAttribute("currentYear", currentYear);

//        model.addAttribute("listSPBanChay",list);
        return "admin/thong_ke/index";
    }

    @GetMapping("/san-pham-ban-chay-api")
    @ResponseBody
    public List<ThongKeSanPhamResponse> getSanPhamBanChayAPI() {
        return thongKeService.getSanPhamBanChay();
    }


}
